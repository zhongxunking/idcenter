/*
 * 作者：钟勋 (email:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2021-07-02 21:14 创建
 */
package org.antframework.idcenter.web.id.support;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务执行器
 */
public class TaskExecutor {
    // 执行器
    private final Executor executor;

    public TaskExecutor(int threads) {
        executor = new ThreadPoolExecutor(
                threads,
                threads,
                5,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 执行
     *
     * @param task 任务
     */
    public void execute(Runnable task) {
        executor.execute(task);
    }
}
