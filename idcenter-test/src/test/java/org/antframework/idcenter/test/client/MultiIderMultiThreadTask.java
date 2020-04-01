/* 
 * 作者：钟勋 (email:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2020-04-01 19:26 创建
 */
package org.antframework.idcenter.test.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antframework.common.util.id.Id;
import org.antframework.idcenter.client.Ider;
import org.antframework.idcenter.client.IderContext;
import org.antframework.idcenter.client.core.DefaultIder;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * 多ider多线程任务
 */
@AllArgsConstructor
@Slf4j
public class MultiIderMultiThreadTask implements Runnable {
    // 序号
    private final int index;
    // ider的数量
    private final int amountOfIder;
    // 线程数量
    private final int amountOfThread;
    // id数量
    private final int amountOfId;
    // 运行结果消费者
    private final Consumer<Performance> consumer;
    // 是否只测性能
    private final boolean onlyPerformance;

    @Override
    public void run() {
        Executor executor = new ThreadPoolExecutor(
                amountOfIder,
                amountOfIder,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(amountOfIder));
        Performance[] performances = new Performance[amountOfIder];
        CountDownLatch latch = new CountDownLatch(amountOfIder);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < amountOfIder; i++) {
            IderContext iderContext = new IderContext("http://localhost:6210", 10 * 60 * 1000, 15 * 60 * 1000, null);
            Ider ider = iderContext.getIder("userId");
            int index = i;
            executor.execute(new SingleIderMultiThreadTask(index, ider, amountOfThread, amountOfId, performance -> {
                performances[index] = performance;
                latch.countDown();
                IderChecker.checkIdAmount((DefaultIder) ider);
            }, onlyPerformance));
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            ExceptionUtils.rethrow(e);
        }
        long endTime = System.currentTimeMillis();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            ExceptionUtils.rethrow(e);
        }
        log.info("----------------多ider多线程任务{}：打印所有单ider多线程任务--start--------------", index);
        Set<Id> idSet = onlyPerformance ? null : new HashSet<>(amountOfIder * amountOfThread * amountOfId);
        int amountOfNullId = 0;
        for (int i = 0; i < amountOfIder; i++) {
            Performance performance = performances[i];
            log.info("单ider多线程任务{}：{}", i, performance);
            if (!onlyPerformance) {
                idSet.addAll(performance.getIdSet());
            }
            amountOfNullId += performance.getAmountOfNullId();
        }
        log.info("----------------多ider多线程任务{}：打印所有单ider多线程任务--end--------------", index);
        Performance performance = new Performance(
                startTime,
                endTime,
                amountOfIder * amountOfThread * amountOfId,
                amountOfNullId,
                idSet);
        log.info("多ider多线程任务{}：{}", index, performance);
        performance.check();
        consumer.accept(performance);
    }
}
