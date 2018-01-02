/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-02 15:38 创建
 */
package org.antframework.ids.client.core;

import org.antframework.ids.client.Id;
import org.antframework.ids.client.IdAcquirer;
import org.antframework.ids.client.IdContext;
import org.antframework.ids.client.support.ServerQuerier;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * id获取器默认实现
 */
public class DefaultIdAcquirer implements IdAcquirer {

    private AcquireTask acquireTask = new AcquireTask();
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            0,
            1,
            5,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.DiscardPolicy());
    private FlowStat flowStat;
    private IdStorage idStorage;
    private ServerQuerier serverQuerier;

    public DefaultIdAcquirer(IdContext.InitParams initParams) {
        flowStat = new FlowStat(initParams);
        idStorage = new IdStorage(initParams);
        serverQuerier = new ServerQuerier(initParams);
    }

    @Override
    public Id getId() {
        flowStat.addCount();
        acquireIfNecessary();
        return idStorage.getId();
    }

    // 从服务端获取id（如果有必要）
    private void acquireIfNecessary() {
        int acquireAmount = flowStat.calcGap(idStorage.getAmount());
        if (acquireAmount > 0) {
            threadPool.execute(acquireTask);
        }
    }

    // 从服务端获取id任务
    private class AcquireTask implements Runnable {
        @Override
        public void run() {
            int acquireAmount = flowStat.calcGap(idStorage.getAmount());
            if (acquireAmount <= 0) {
                for (Ids ids : serverQuerier.acquireIds(acquireAmount)) {
                    idStorage.addIds(ids);
                }
            }
        }
    }

    //流量统计
    private static class FlowStat {
        // 最大时间（毫秒）
        private final long maxTime;
        // 最小时间（毫秒）
        private final long minTime;
        // 统计开始时间
        private long startTime;
        // id使用量统计
        private long count;
        // 下一个统计开始时间
        private long nextStartTime;
        // 下一个id使用量统计
        private long nextCount;

        public FlowStat(IdContext.InitParams initParams) {
            maxTime = initParams.getMaxTime();
            minTime = initParams.getMinTime();
            startTime = System.currentTimeMillis();
            count = 0;
            nextStartTime = System.currentTimeMillis();
            nextCount = 0;
        }

        /**
         * 增加统计
         */
        public void addCount() {
            count++;
            nextCount++;
        }

        /**
         * 切换到下一个统计
         */
        public void next() {
            startTime = nextStartTime;
            count = nextCount;
            nextStartTime = System.currentTimeMillis();
            nextCount = 0;
        }

        /**
         * 计算缺口
         *
         * @param remainAmount 剩余数量
         * @return 缺口
         */
        public int calcGap(int remainAmount) {
            long statDuration = System.currentTimeMillis() - startTime;
            int min = (int) (((double) minTime) / statDuration * count);
            if (remainAmount >= min) {
                return 0;
            }
            int max = (int) (((double) maxTime) / statDuration * count);
            return max - remainAmount;
        }
    }
}
