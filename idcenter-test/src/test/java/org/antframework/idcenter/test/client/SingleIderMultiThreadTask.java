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
import org.antframework.idcenter.client.IderContext;
import org.antframework.idcenter.client.core.DefaultIder;
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
    // 服务端地址
    private final String serverUrl;
    // id提供者的id（id编码）
    private final String iderId;
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
        IderContext iderContext = new IderContext(serverUrl, 10 * 60 * 1000, 15 * 60 * 1000, null);
        Ider ider = iderContext.getIder(iderId);

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

        log.info("----------------单ider多线程任务{}：打印所有单ider单线程任务--start--------------", index);
        Set<Id> idSet = onlyPerformance ? null : new HashSet<>(amountOfThread * amountOfId);
        int amountOfNullId = 0;
        double tps = 0;
        for (int i = 0; i < amountOfThread; i++) {
            Performance performance = performances[i];
            log.info("单ider单线程任务{}：{}", i, performance);
            if (!onlyPerformance) {
                idSet.addAll(performance.getIdSet());
            }
            amountOfNullId += performance.getAmountOfNullId();
            tps += performance.getTps();
        }
        log.info("----------------单ider多线程任务{}：打印所有单ider单线程任务--end--------------", index);
        Performance performance = new Performance(
                startTime,
                endTime,
                amountOfThread * amountOfId,
                amountOfNullId,
                idSet,
                tps);
        log.info("单ider多线程任务{}：{}", index, performance);
        performance.check();
        IderChecker.checkIdAmount((DefaultIder) ider);
        consumer.accept(performance);
    }
}
