/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-02 15:38 创建
 */
package org.antframework.idcenter.client.core;

import org.antframework.idcenter.client.Id;
import org.antframework.idcenter.client.IdContext;
import org.antframework.idcenter.client.support.FlowStat;
import org.antframework.idcenter.client.support.IdStorage;
import org.antframework.idcenter.client.support.ServerRequester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * id获取器默认实现
 */
public class DefaultIdAcquirer implements ConfigurableIdAcquirer {
    private static final Logger logger = LoggerFactory.getLogger(DefaultIdAcquirer.class);

    // 从服务端获取id任务线程池
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            0,
            1,
            5,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.DiscardPolicy());
    // 从服务端获取id任务
    private AcquireTask acquireTask = new AcquireTask();
    // 流量统计
    private FlowStat flowStat;
    // id仓库
    private IdStorage idStorage;
    // 服务端请求器
    private ServerRequester serverRequester;

    public DefaultIdAcquirer(IdContext.InitParams initParams) {
        flowStat = new FlowStat(initParams);
        idStorage = new IdStorage(initParams);
        serverRequester = new ServerRequester(initParams);
        // 初始化获取id
        acquire(initParams.getInitAmount());
    }

    @Override
    public synchronized Id getId() {
        flowStat.addCount();
        acquireIfNecessary();
        return idStorage.getId();
    }

    @Override
    public void close() {
        threadPool.shutdown();
    }

    // 从服务端获取id（如果有必要）
    private void acquireIfNecessary() {
        int gap = flowStat.calcGap(idStorage.getAmount(true));
        if (gap > 0) {
            threadPool.execute(acquireTask);
        }
    }

    // 从服务端获取id
    private void acquire(int acquireAmount) {
        try {
            for (Ids ids : serverRequester.acquireIds(acquireAmount)) {
                idStorage.addIds(ids);
            }
            flowStat.next();
        } catch (Throwable e) {
            logger.error("从服务端获取id出错：{}", e.getMessage());
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
