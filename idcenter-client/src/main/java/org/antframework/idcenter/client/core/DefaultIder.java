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
import java.util.List;
import java.util.concurrent.*;

/**
 * id提供者默认实现
 */
@Slf4j
public class DefaultIder implements Ider {
    // 最短间隔时间（毫秒）
    private static final long MIN_INTERVAL = 5000;

    // 从服务端获取id任务的线程池
    private final Executor executor = new ThreadPoolExecutor(
            0,
            1,
            5,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.DiscardPolicy());
    // 上次因id过期从服务端获取id的时间
    private volatile long lastOverdueTime = System.currentTimeMillis() - MIN_INTERVAL;
    // 上次请求服务端失败的时间
    private volatile long lastServerFailureTime = lastOverdueTime;
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
    // 服务端请求器
    private final ServerRequester serverRequester;

    /**
     * 构造id提供者
     *
     * @param iderId            id提供者的id（id编码）
     * @param minDuration       最小预留时间（毫秒）
     * @param maxDuration       最大预留时间（毫秒）
     * @param maxBlockedThreads 最多被阻塞的线程数量（null表示不限制数量）
     * @param serverRequester   服务端请求器
     */
    public DefaultIder(String iderId, long minDuration, long maxDuration, Integer maxBlockedThreads, ServerRequester serverRequester) {
        this.iderId = iderId;
        this.flowCounter = new FlowCounter(minDuration, maxDuration);
        this.semaphore = maxBlockedThreads == null ? null : new Semaphore(maxBlockedThreads);
        this.serverRequester = serverRequester;
    }

    @Override
    public Id acquire() {
        flowCounter.addCount(1);
        asyncAcquireIds(true);
        Id id = idStorage.getId();
        if (id == null) {
            syncAcquireIds(true);
            id = idStorage.getId();
        }
        if (id != null && isOverdue(id)) {
            asyncAcquireIds(false);
        }
        return id;
    }

    // id是否过期
    private boolean isOverdue(Id id) {
        // 对频繁过期情况限速
        long currentTime = System.currentTimeMillis();
        if (lastOverdueTime > currentTime) {
            lastOverdueTime = currentTime;
        }
        if (currentTime - lastOverdueTime < MIN_INTERVAL) {
            return false;
        }
        // 判断是否过期
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
                    acquireIdsFromServer(gap);
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

    // 从服务端获取批量id
    private void acquireIdsFromServer(int amount) {
        // 对失败情况限速
        long currentTime = System.currentTimeMillis();
        if (lastServerFailureTime > currentTime) {
            lastServerFailureTime = currentTime;
        }
        if (currentTime - lastServerFailureTime < MIN_INTERVAL) {
            return;
        }
        // 获取批量id
        try {
            List<ServerRequester.IdSegment> idSegments = serverRequester.acquireIds(iderId, amount);
            idSegments.stream()
                    .map(idSegment -> new IdChunk(idSegment.getPeriod(), idSegment.getFactor(), idSegment.getStartId(), idSegment.getAmount()))
                    .forEach(idStorage::addIdChunk);
            flowCounter.next();
        } catch (Throwable e) {
            lastServerFailureTime = System.currentTimeMillis();
            log.error("从idcenter获取id出错：{}", e.getMessage());
        }
    }
}
