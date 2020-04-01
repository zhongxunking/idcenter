/* 
 * 作者：钟勋 (email:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2020-04-01 19:18 创建
 */
package org.antframework.idcenter.test.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antframework.common.util.id.Id;
import org.antframework.idcenter.client.Ider;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * 单ider多线程任务
 */
@AllArgsConstructor
@Slf4j
public class SingleIderMultiThreadTask implements Runnable {
    // 任务序号
    private final int index;
    // id提供者
    private final Ider ider;
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
                amountOfThread,
                amountOfThread,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(amountOfThread));
        Performance[] performances = new Performance[amountOfThread];
        CountDownLatch latch = new CountDownLatch(amountOfThread);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < amountOfThread; i++) {
            int index = i;
            executor.execute(new SingleIderSingleThreadTask(index, ider, amountOfId, performance -> {
                performances[index] = performance;
                latch.countDown();
            }, onlyPerformance));
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            ExceptionUtils.rethrow(e);
        }
        long endTime = System.currentTimeMillis();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            ExceptionUtils.rethrow(e);
        }
        log.info("----------------打印所有单ider单线程任务--start--------------");
        Set<Id> idSet = onlyPerformance ? null : new HashSet<>(amountOfThread * amountOfId);
        int amountOfNullId = 0;
        for (Performance performance : performances) {
            log.info("单ider单线程任务：{}", performance);
            if (!onlyPerformance) {
                idSet.addAll(performance.getIdSet());
            }
            amountOfNullId += performance.getAmountOfNullId();
        }
        log.info("----------------打印所有单ider单线程任务--end--------------");
        Performance performance = new Performance(index, startTime, endTime, amountOfThread * amountOfId, amountOfNullId, idSet);
        log.info("单ider多线程任务：{}", performance);
        performance.check();
        consumer.accept(performance);
    }
}
