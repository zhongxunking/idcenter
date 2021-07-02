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
import org.antframework.common.util.other.RateLimiter;
import org.antframework.idcenter.client.Ider;
import org.antframework.idcenter.client.support.FlowCounter;
import org.antframework.idcenter.client.support.ServerRequester;
import org.antframework.idcenter.client.support.TaskExecutor;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * id提供者默认实现
 */
@Slf4j
public class DefaultIder implements Ider {
    // 过期限速器
    private final RateLimiter overdueRateLimiter = new RateLimiter(5000);
    // 服务端失败限速器
    private final RateLimiter serverFailureRateLimiter = new RateLimiter(5000);
    // 是否存在异步任务
    private final AtomicBoolean existingAsyncTask = new AtomicBoolean(false);
    // 请求服务端的互斥锁
    private final Object requestServerLock = new Object();
    // id仓库
    private final IdStorage idStorage = new IdStorage();
    // id提供者的id（id编码）
    private final String iderId;
    // 流量计数器
    private final FlowCounter flowCounter;
    // 限制被阻塞线程数量的信号量
    private final Semaphore limitedThreadsSemaphore;
    // 服务端请求器
    private final ServerRequester serverRequester;
    // 任务执行器
    private final TaskExecutor taskExecutor;

    /**
     * 构造id提供者
     *
     * @param iderId            id提供者的id（id编码）
     * @param minReserve        最短时长储备量（毫秒）
     * @param maxReserve        最长时长储备量（毫秒）
     * @param maxBlockedThreads 最多被阻塞的线程数量（null表示不限制数量）
     * @param serverRequester   服务端请求器
     * @param taskExecutor      任务执行器
     */
    public DefaultIder(String iderId,
                       long minReserve,
                       long maxReserve,
                       Integer maxBlockedThreads,
                       ServerRequester serverRequester,
                       TaskExecutor taskExecutor) {
        this.iderId = iderId;
        this.flowCounter = new FlowCounter(minReserve, maxReserve);
        this.limitedThreadsSemaphore = maxBlockedThreads == null ? null : new Semaphore(maxBlockedThreads);
        this.serverRequester = serverRequester;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public Id acquireId() {
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
        if (!overdueRateLimiter.can()) {
            return false;
        }
        Period currentPeriod = new Period(id.getPeriod().getType(), new Date());
        if (currentPeriod.compareTo(id.getPeriod()) <= 0) {
            return false;
        }
        overdueRateLimiter.run();
        return true;
    }

    // 异步从服务端获取批量id（如果有必要）
    private void asyncAcquireIds(boolean checkGap) {
        if (checkGap) {
            int gap = flowCounter.computeGap(idStorage.getAmount(null));
            if (gap <= 0) {
                return;
            }
        }
        if (existingAsyncTask.compareAndSet(false, true)) {
            taskExecutor.execute(() -> {
                try {
                    syncAcquireIds(false);
                } finally {
                    existingAsyncTask.set(false);
                }
            });
        }
    }

    // 同步从服务端获取批量id（如果有必要）
    private void syncAcquireIds(boolean limit) {
        if (limit && limitedThreadsSemaphore != null) {
            if (!limitedThreadsSemaphore.tryAcquire()) {
                return;
            }
        }
        try {
            synchronized (requestServerLock) {
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
            if (limit && limitedThreadsSemaphore != null) {
                limitedThreadsSemaphore.release();
            }
        }
    }

    // 从服务端获取批量id
    private void acquireIdsFromServer(int amount) {
        if (!serverFailureRateLimiter.can()) {
            return;
        }
        try {
            List<ServerRequester.IdSegment> idSegments = serverRequester.acquireIds(iderId, amount);
            idSegments.stream()
                    .map(idSegment -> new IdChunk(idSegment.getPeriod(), idSegment.getFactor(), idSegment.getStartId(), idSegment.getAmount()))
                    .forEach(idStorage::addIdChunk);
            flowCounter.next();
        } catch (Throwable e) {
            serverFailureRateLimiter.run();
            log.error("从idcenter获取id出错：{}", e.getMessage());
        }
    }
}
