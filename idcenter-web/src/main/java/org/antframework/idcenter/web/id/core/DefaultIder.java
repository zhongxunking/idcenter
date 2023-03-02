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
import org.antframework.common.util.kit.RateLimiter;
import org.antframework.idcenter.biz.util.Iders;
import org.antframework.idcenter.facade.vo.IdSegment;
import org.antframework.idcenter.web.id.Ider;
import org.antframework.idcenter.web.id.support.FlowCounter;
import org.antframework.idcenter.web.id.support.TaskExecutor;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * id提供者默认实现
 */
@Slf4j
public class DefaultIder implements Ider {
    // 服务失败限速器
    private final RateLimiter serviceFailureRateLimiter = new RateLimiter(5000);
    // 是否存在异步任务
    private final AtomicBoolean existingAsyncTask = new AtomicBoolean(false);
    // 请求服务的互斥锁
    private final Object requestServiceLock = new Object();
    // id仓库
    private final IdStorage idStorage = new IdStorage();
    // id提供者的id（id编码）
    private final String iderId;
    // 流量计数器
    private final FlowCounter flowCounter;
    // 限制被阻塞线程数量的信号量
    private final Semaphore limitedThreadsSemaphore;
    // 任务执行器
    private final TaskExecutor taskExecutor;

    /**
     * 构造id提供者
     *
     * @param iderId            id提供者的id（id编码）
     * @param minReserve        最短时长储备量（毫秒）
     * @param maxReserve        最长时长储备量（毫秒）
     * @param maxBlockedThreads 最多被阻塞的线程数量（null表示不限制数量）
     * @param taskExecutor      任务执行器
     */
    public DefaultIder(String iderId,
                       long minReserve,
                       long maxReserve,
                       Integer maxBlockedThreads,
                       TaskExecutor taskExecutor) {
        this.iderId = iderId;
        this.flowCounter = new FlowCounter(minReserve, maxReserve);
        this.limitedThreadsSemaphore = maxBlockedThreads == null ? null : new Semaphore(maxBlockedThreads);
        this.taskExecutor = taskExecutor;
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

    // 异步从服务获取批量id（如果有必要）
    private void asyncAcquireIds(boolean checkGap) {
        if (checkGap) {
            int gap = flowCounter.computeGap(idStorage.getAmount(null));
            if (gap <= 0) {
                return;
            }
        }
        if (existingAsyncTask.compareAndSet(false, true)) {
            try {
                taskExecutor.execute(() -> {
                    try {
                        syncAcquireIds(false);
                    } finally {
                        existingAsyncTask.set(false);
                    }
                });
            } catch (Throwable e) {
                existingAsyncTask.set(false);
                log.error("触发异步获取批量id任务失败", e);
            }
        }
    }

    // 同步从服务获取批量id（如果有必要）
    private void syncAcquireIds(boolean limit) {
        if (limit && limitedThreadsSemaphore != null) {
            if (!limitedThreadsSemaphore.tryAcquire()) {
                return;
            }
        }
        try {
            synchronized (requestServiceLock) {
                Long currentTime = System.currentTimeMillis();
                int gap = flowCounter.computeGap(idStorage.getAmount(currentTime));
                if (gap > 0) {
                    acquireIdsFromService(gap);
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

    // 从服务获取批量id
    private void acquireIdsFromService(int amount) {
        if (!serviceFailureRateLimiter.can()) {
            return;
        }
        try {
            List<IdSegment> idSegments = Iders.acquireIds(iderId, amount);
            idSegments.stream()
                    .map(idSegment -> new IdChunk(idSegment.getPeriod(), idSegment.getFactor(), idSegment.getStartId(), idSegment.getAmount()))
                    .forEach(idStorage::addIdChunk);
            flowCounter.next();
        } catch (Throwable e) {
            serviceFailureRateLimiter.run();
            log.error("从service获取id出错：{}", e.toString());
        }
    }
}
