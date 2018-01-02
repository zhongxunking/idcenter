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
import org.antframework.ids.client.support.FlowStat;
import org.antframework.ids.client.support.IdStorage;
import org.antframework.ids.client.support.ServerRequester;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * id获取器默认实现
 */
public class DefaultIdAcquirer implements IdAcquirer {
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
            if (acquireAmount > 0) {
                for (Ids ids : serverRequester.acquireIds(acquireAmount)) {
                    idStorage.addIds(ids);
                }
                flowStat.next();
            }
        }
    }
}
