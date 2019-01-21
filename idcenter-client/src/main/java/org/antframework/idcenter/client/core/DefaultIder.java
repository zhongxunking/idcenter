/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-20 23:16 创建
 */
package org.antframework.idcenter.client.core;

import org.antframework.common.util.id.Id;
import org.antframework.idcenter.client.Ider;
import org.antframework.idcenter.client.support.FlowStat;
import org.antframework.idcenter.client.support.ServerRequester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * id提供者默认实现
 */
public class DefaultIder implements Ider {
    private static final Logger logger = LoggerFactory.getLogger(DefaultIder.class);
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
    // id提供者的id（id编码）
    private final String iderId;
    // 流量统计
    private final FlowStat flowStat;
    // id仓库
    private final IdStorage idStorage;
    // 服务端请求器
    private final ServerRequester serverRequester;

    public DefaultIder(String iderId, long minDuration, long maxDuration, ServerRequester serverRequester) {
        this.iderId = iderId;
        flowStat = new FlowStat(minDuration, maxDuration);
        idStorage = new IdStorage(maxDuration - minDuration);
        this.serverRequester = serverRequester;
    }

    @Override
    public Id acquire() {
        flowStat.addCount();
        asyncAcquireIds();
        Id id = idStorage.getId();
        if (id == null) {
            syncAcquireIds();
            id = idStorage.getId();
        }
        return id;
    }

    // 异步从服务端获取id（如果有必要）
    private void asyncAcquireIds() {
        int gap = flowStat.calcGap(idStorage.getAmount(true));
        if (gap > 0) {
            executor.execute(this::syncAcquireIds);
        }
    }

    // 同步从服务端获取id（如果有必要）
    private void syncAcquireIds() {
        if (!semaphore.tryAcquire()) {
            return;
        }
        try {
            synchronized (lock) {
                int gap = flowStat.calcGap(idStorage.getAmount(false));
                if (gap > 0) {
                    acquireIds(gap);
                }
            }
        } finally {
            semaphore.release();
        }
    }

    // 从服务端获取id
    private void acquireIds(int amount) {
        try {
            for (Ids ids : serverRequester.acquireIds(iderId, amount)) {
                idStorage.addIds(ids);
            }
            flowStat.next();
        } catch (Throwable e) {
            logger.error("从id中心获取id出错：{}", e.getMessage());
        }
    }
}
