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
    // 服务端地址
    private final String serverUrl;
    // id提供者的id（id编码）
    private final String iderId;
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
            int index = i;
            executor.execute(new SingleIderMultiThreadTask(index, serverUrl, iderId, amountOfThread, amountOfId, performance -> {
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

        log.info("----------------多ider多线程任务{}：打印所有单ider多线程任务--start--------------", index);
        Set<Id> idSet = onlyPerformance ? null : new HashSet<>(amountOfIder * amountOfThread * amountOfId);
        int amountOfNullId = 0;
        double tps = 0;
        for (int i = 0; i < amountOfIder; i++) {
            Performance performance = performances[i];
            log.info("单ider多线程任务{}：{}", i, performance);
            if (!onlyPerformance) {
                idSet.addAll(performance.getIdSet());
            }
            amountOfNullId += performance.getAmountOfNullId();
            tps += performance.getTps();
        }
        log.info("----------------多ider多线程任务{}：打印所有单ider多线程任务--end--------------", index);
        Performance performance = new Performance(
                startTime,
                endTime,
                amountOfIder * amountOfThread * amountOfId,
                amountOfNullId,
                idSet,
                tps);
        log.info("多ider多线程任务{}：{}", index, performance);
        performance.check();
        consumer.accept(performance);
    }
}
