/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-20 23:16 创建
 */
package org.antframework.idcenter.client.core;

import lombok.extern.slf4j.Slf4j;
import org.antframework.common.util.id.Id;
import org.antframework.common.util.id.Period;
import org.antframework.idcenter.client.Ider;
import org.antframework.idcenter.client.support.FlowCounter;
import org.antframework.idcenter.client.support.ServerRequester;

import java.util.Date;
import java.util.concurrent.*;

/**
 * id提供者默认实现
 */
@Slf4j
public class DefaultIder implements Ider {
    // 因id过期从服务端获取id最短间隔时间（毫秒）
    private static final long MIN_OVERDUE_INTERVAL = 5000;

    // 从服务端获取id任务的线程池
    private final Executor executor = new ThreadPoolExecutor(
            0,
            1,
            5,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.DiscardPolicy());
    // 限制等待线程数量的信号量
    private final Semaphore semaphore = new Semaphore(10);
    // 请求服务端的互斥锁
    private final Object lock = new Object();
    // 上次因id过期从服务端获取id的时间
    private volatile long lastOverdueTime = System.currentTimeMillis() - MIN_OVERDUE_INTERVAL;
    // id仓库
    private final IdStorage idStorage = new IdStorage();
    // id提供者的id（id编码）
    private final String iderId;
    // 流量计数器
    private final FlowCounter flowCounter;
    // 服务端请求器
    private final ServerRequester serverRequester;

    /**
     * 构造id提供者
     *
     * @param iderId          id提供者的id（id编码）
     * @param minDuration     最小预留时间（毫秒）
     * @param maxDuration     最大预留时间（毫秒）
     * @param serverRequester 服务端请求器
     */
    public DefaultIder(String iderId, long minDuration, long maxDuration, ServerRequester serverRequester) {
        this.iderId = iderId;
        this.flowCounter = new FlowCounter(minDuration, maxDuration);
        this.serverRequester = serverRequester;
    }

    @Override
    public Id acquire() {
        flowCounter.addCount();
        asyncAcquireIds(true);
        Id id = idStorage.getId();
        if (id == null) {
            syncAcquireIds();
            id = idStorage.getId();
        }
        if (id != null && isOverdue(id)) {
            asyncAcquireIds(false);
        }
        return id;
    }

    // id是否过期
    private boolean isOverdue(Id id) {
        long currentTime = System.currentTimeMillis();
        if (lastOverdueTime > currentTime) {
            lastOverdueTime = currentTime;
        }
        if (currentTime - lastOverdueTime < MIN_OVERDUE_INTERVAL) {
            return false;
        }
        Period currentPeriod = new Period(id.getPeriod().getType(), new Date(currentTime));
        if (currentPeriod.compareTo(id.getPeriod()) <= 0) {
            return false;
        }
        lastOverdueTime = currentTime;
        return true;
    }

    // 异步从服务端获取id（如果有必要）
    private void asyncAcquireIds(boolean checkGap) {
        if (checkGap) {
            int gap = flowCounter.computeGap(idStorage.getAmount(null));
            if (gap <= 0) {
                return;
            }
        }
        executor.execute(this::syncAcquireIds);
    }

    // 同步从服务端获取id（如果有必要）
    private void syncAcquireIds() {
        if (!semaphore.tryAcquire()) {
            return;
        }
        try {
            synchronized (lock) {
                Long currentTime = System.currentTimeMillis();
                int gap = flowCounter.computeGap(idStorage.getAmount(currentTime));
                if (gap > 0) {
                    acquireIds(gap);
                    gap = flowCounter.computeGap(idStorage.getAmount(currentTime));
                }
                if (gap <= 0) {
                    idStorage.clearOverdueIdChunks(currentTime);
                }
            }
        } finally {
            semaphore.release();
        }
    }

    // 从服务端获取id
    private void acquireIds(int amount) {
        try {
            for (ServerRequester.Ids ids : serverRequester.acquireIds(iderId, amount)) {
                IdChunk idChunk = new IdChunk(ids.getPeriod(), ids.getFactor(), ids.getStartId(), ids.getAmount());
                idStorage.addIdChunk(idChunk);
            }
            flowCounter.next();
        } catch (Throwable e) {
            log.error("从idcenter获取id出错：{}", e.getMessage());
        }
    }
}
