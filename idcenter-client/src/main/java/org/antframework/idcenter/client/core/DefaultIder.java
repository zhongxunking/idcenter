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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * id提供者默认实现
 */
public class DefaultIder implements Ider {
    private static final Logger logger = LoggerFactory.getLogger(DefaultIder.class);
    // 从服务端获取id任务线程池
    private final Executor executor = new ThreadPoolExecutor(
            0,
            1,
            5,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.DiscardPolicy());
    // 从服务端获取id任务
    private final AcquireTask acquireTask = new AcquireTask();
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
    public synchronized Id acquire() {
        flowStat.addCount();
        acquireIfNecessary();
        return idStorage.getId();
    }

    // 从服务端获取id（如果有必要）
    private void acquireIfNecessary() {
        int gap = flowStat.calcGap(idStorage.getAmount(true));
        if (gap > 0) {
            executor.execute(acquireTask);
        }
    }

    // 从服务端获取id
    private void acquire(int amount) {
        try {
            for (Ids ids : serverRequester.acquireIds(iderId, amount)) {
                idStorage.addIds(ids);
            }
            flowStat.next();
        } catch (Throwable e) {
            logger.error("从id中心获取id出错：{}", e.getMessage());
        }
    }

    // 从服务端获取id任务
    private class AcquireTask implements Runnable {
        @Override
        public void run() {
            int gap = flowStat.calcGap(idStorage.getAmount(false));
            if (gap > 0) {
                acquire(gap);
            }
        }
    }
}
