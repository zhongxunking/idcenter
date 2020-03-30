/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-20 23:16 创建
 */
package org.antframework.idcenter.web.id.core;

import lombok.extern.slf4j.Slf4j;
import org.antframework.common.util.id.Period;
import org.antframework.idcenter.biz.util.Iders;
import org.antframework.idcenter.facade.vo.IdSegment;
import org.antframework.idcenter.web.id.Ider;
import org.antframework.idcenter.web.id.support.FlowCounter;

import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * id提供者默认实现
 */
@Slf4j
public class DefaultIder implements Ider {
    // 从服务端获取id任务的线程池
    private final Executor executor = new ThreadPoolExecutor(
            0,
            1,
            5,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.DiscardPolicy());
    // 请求服务端的互斥锁
    private final Object lock = new Object();
    // id仓库
    private final IdStorage idStorage = new IdStorage();
    // id提供者的id（id编码）
    private final String iderId;
    // 流量计数器
    private final FlowCounter flowCounter;
    // 限制等待线程数量的信号量
    private final Semaphore semaphore;

    /**
     * 构造id提供者
     *
     * @param iderId            id提供者的id（id编码）
     * @param minDuration       最小预留时间（毫秒）
     * @param maxDuration       最大预留时间（毫秒）
     * @param maxBlockedThreads 最多被阻塞的线程数量（null表示不限制数量）
     */
    public DefaultIder(String iderId, long minDuration, long maxDuration, Integer maxBlockedThreads) {
        this.iderId = iderId;
        this.flowCounter = new FlowCounter(minDuration, maxDuration);
        this.semaphore = maxBlockedThreads == null ? null : new Semaphore(maxBlockedThreads);
    }

    @Override
    public List<IdSegment> acquireIds(int amount) {
        flowCounter.addCount(amount);
        asyncAcquireIds(true);
        List<IdSegment> idSegments = idStorage.getIds(amount);
        if (idSegments.isEmpty()) {
            syncAcquireIds(true);
            idSegments = idStorage.getIds(amount);
        }
        if (!idSegments.isEmpty() && isOverdue(idSegments.get(0))) {
            asyncAcquireIds(false);
        }
        return idSegments;
    }

    // id是否过期
    private boolean isOverdue(IdSegment idSegment) {
        Period currentPeriod = new Period(idSegment.getPeriod().getType(), new Date());
        return currentPeriod.compareTo(idSegment.getPeriod()) > 0;
    }

    // 异步从服务端获取id（如果有必要）
    private void asyncAcquireIds(boolean checkGap) {
        if (checkGap) {
            int gap = flowCounter.computeGap(idStorage.getAmount(null));
            if (gap <= 0) {
                return;
            }
        }
        executor.execute(() -> syncAcquireIds(false));
    }

    // 同步从服务端获取id（如果有必要）
    private void syncAcquireIds(boolean limit) {
        if (limit && semaphore != null) {
            if (!semaphore.tryAcquire()) {
                return;
            }
        }
        try {
            synchronized (lock) {
                Long currentTime = System.currentTimeMillis();
                int gap = flowCounter.computeGap(idStorage.getAmount(currentTime));
                if (gap > 0) {
                    doAcquireIds(gap);
                    gap = flowCounter.computeGap(idStorage.getAmount(currentTime));
                }
                if (gap <= 0) {
                    idStorage.clearOverdueIdChunks(currentTime);
                }
            }
        } finally {
            if (limit && semaphore != null) {
                semaphore.release();
            }
        }
    }

    // 从服务端获取id
    private void doAcquireIds(int amount) {
        try {
            List<IdSegment> idSegments = Iders.acquireIds(iderId, amount);
            idSegments.stream()
                    .map(idSegment -> new IdChunk(idSegment.getPeriod(), idSegment.getFactor(), idSegment.getStartId(), idSegment.getAmount()))
                    .forEach(idStorage::addIdChunk);
            flowCounter.next();
        } catch (Throwable e) {
            log.error("从idcenter获取id出错：{}", e.getMessage());
        }
    }
}
