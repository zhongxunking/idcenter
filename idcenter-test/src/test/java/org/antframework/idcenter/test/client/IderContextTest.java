/*
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-03 00:19 创建
 */
package org.antframework.idcenter.test.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.antframework.common.util.id.Id;
import org.antframework.idcenter.client.Ider;
import org.antframework.idcenter.client.IderContext;
import org.antframework.idcenter.client.core.DefaultIder;
import org.antframework.idcenter.client.core.IdChunk;
import org.antframework.idcenter.client.core.IdStorage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * id提供者上下文单元测试
 */
@Ignore
public class IderContextTest {
    private static final Logger logger = LoggerFactory.getLogger(IderContextTest.class);

    @Test
    public void testIdersContext() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        SingleThreadSingleIderTest.test();
//        SingleIderMultiThreadTest.test();
//        MultiIderMultiThreadTest.test();
    }

    // 单ider单线程--测试
    private static class SingleThreadSingleIderTest {
        // 循环次数
        private static final int COUNT = 1000000;

        static void test() {
            IderContext iderContext = new IderContext("http://localhost:6210", 10 * 60 * 1000, 15 * 60 * 1000, null);
            Ider ider = iderContext.getIder("userId");
            logger.info("-----------单ider单线程-----------start");
            new SingleIderSingleThreadTask(0, ider, COUNT, performance -> {
            }).run();
            checkAmount((DefaultIder) ider);
            logger.info("-----------单ider单线程-----------end");
        }
    }

    // 单ider单线程任务
    @AllArgsConstructor
    private static class SingleIderSingleThreadTask implements Runnable {
        // 任务序号
        private int index;
        // id提供者
        private Ider ider;
        // 循环次数
        private int count;
        // 运行结果消费者
        private Consumer<Performance> consumer;

        @Override
        public void run() {
            Set<Id> idSet = new HashSet<>(count);
            int nullCount = 0;
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                Id id = ider.acquire();
                if (id != null) {
                    idSet.add(id);
                } else {
                    nullCount++;
                }
            }
            long endTime = System.currentTimeMillis();

            Performance performance = new Performance(index, startTime, endTime, count, nullCount, idSet);
            logger.info("单ider单线程任务：{}", performance);
            performance.check();
            consumer.accept(performance);
        }
    }

    // 单ider多线程--测试
    private static class SingleIderMultiThreadTest {
        // 线程数量
        private static final int AMOUNT_OF_THREAD = 30;

        static void test() {
            IderContext iderContext = new IderContext("http://localhost:6210", 10 * 60 * 1000, 15 * 60 * 1000, null);
            Ider ider = iderContext.getIder("userId");
            logger.info("-----------单ider多线程-----------start");
            new SingleIderMultiThreadTask(0, ider, AMOUNT_OF_THREAD, performance -> {
            }).run();
            checkAmount((DefaultIder) ider);
            logger.info("-----------单ider多线程-----------end");
        }
    }

    // 单ider多线程任务
    @AllArgsConstructor
    private static class SingleIderMultiThreadTask implements Runnable {
        // 线程内循环次数
        private static final int COUNT_IN_THREAD = 100000;
        // 任务序号
        private int index;
        // id提供者
        private Ider ider;
        // 线程数量
        private int amountOfThread;
        // 运行结果消费者
        private Consumer<Performance> consumer;

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
                executor.execute(new SingleIderSingleThreadTask(index, ider, COUNT_IN_THREAD, performance -> {
                    performances[index] = performance;
                    latch.countDown();
                }));
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
            logger.info("----------------打印所有单ider单线程任务--start--------------");
            Set<Id> idSet = new HashSet<>(amountOfThread * COUNT_IN_THREAD);
            int nullCount = 0;
            for (Performance performance : performances) {
                logger.info("单ider单线程任务：{}", performance);
                idSet.addAll(performance.getIdSet());
                nullCount += performance.getNullCount();
            }
            logger.info("----------------打印所有单ider单线程任务--end--------------");
            Performance performance = new Performance(index, startTime, endTime, amountOfThread * COUNT_IN_THREAD, nullCount, idSet);
            logger.info("单ider多线程任务：{}", performance);
            performance.check();
            consumer.accept(performance);
        }
    }

    private static class MultiIderMultiThreadTest {

        static void test() {
            logger.info("------------多ider多线程-------------start");
            new MultiIderMultiThreadTask(0, 10, performance -> {
            }).run();
            logger.info("------------多ider多线程-------------end");
        }
    }

    // 多ider多线程任务
    @AllArgsConstructor
    private static class MultiIderMultiThreadTask implements Runnable {
        // 每个ider任务的线程数量
        private static final int AMOUNT_OF_THREAD = 20;
        // 序号
        private int index;
        // ider的数量
        private int amountOfIder;
        // 运行结果消费者
        private Consumer<Performance> consumer;

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
                executor.execute(new SingleIderMultiThreadTask(index, ider, AMOUNT_OF_THREAD, performance -> {
                    performances[index] = performance;
                    latch.countDown();
                    checkAmount((DefaultIder) ider);
                }));
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
            logger.info("----------------打印所有单ider多线程任务--start--------------");
            Set<Id> idSet = new HashSet<>(amountOfIder * AMOUNT_OF_THREAD * SingleIderMultiThreadTask.COUNT_IN_THREAD);
            int nullCount = 0;
            for (Performance performance : performances) {
                logger.info("单ider多线程任务：{}", performance);
                idSet.addAll(performance.getIdSet());
                nullCount += performance.getNullCount();
            }
            logger.info("----------------打印所有单ider多线程任务--end--------------");
            Performance performance = new Performance(
                    index,
                    startTime,
                    endTime,
                    amountOfIder * AMOUNT_OF_THREAD * SingleIderMultiThreadTask.COUNT_IN_THREAD,
                    nullCount,
                    idSet);
            logger.info("多ider多线程任务：{}", performance);
            performance.check();
            consumer.accept(performance);
        }
    }

    // 校验id余量
    private static void checkAmount(DefaultIder ider) {
        try {
            Thread.sleep(5000);
            Field idStorageField = DefaultIder.class.getDeclaredField("idStorage");
            ReflectionUtils.makeAccessible(idStorageField);
            IdStorage idStorage = (IdStorage) idStorageField.get(ider);

            Field amountField = IdStorage.class.getDeclaredField("amount");
            ReflectionUtils.makeAccessible(amountField);
            AtomicLong amount = (AtomicLong) amountField.get(idStorage);

            Field idChunksField = IdStorage.class.getDeclaredField("idChunks");
            ReflectionUtils.makeAccessible(idChunksField);
            Queue<IdChunk> idChunks = (Queue<IdChunk>) idChunksField.get(idStorage);

            long realAmount = 0;
            for (IdChunk idChunk : idChunks) {
                realAmount += idChunk.getAmount(null);
            }

            logger.info("校验IdStorage：id仓库记录余量={}，id真正余量={}，idChunks大小={}", amount.get(), realAmount, idChunks.size());
            Assert.assertEquals(realAmount, amount.get());
        } catch (Throwable e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @AllArgsConstructor
    @Getter
    private static class Performance {
        // 任务序号
        private int index;
        // 开始时间
        private long startTime;
        // 结束时间
        private long endTime;
        // 循环次数
        private int count;
        // id出现null次数
        private int nullCount;
        // id集合
        private Set<Id> idSet;

        long getTimeCost() {
            return endTime - startTime;
        }

        double getTps() {
            return (count - nullCount) * 1000D / getTimeCost();
        }

        void check() {
            Assert.assertEquals(count - nullCount, idSet.size());
        }

        @Override
        public String toString() {
            return String.format("任务序号=%d，开始时间=%s，结束时间=%s，耗时=%d毫秒，循环次数=%d，id出现null次数=%d，id数量=%d，tps=%.2f",
                    index,
                    DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm:ss.SSS"),
                    DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm:ss.SSS"),
                    getTimeCost(),
                    count,
                    nullCount,
                    idSet.size(),
                    getTps());
        }
    }
}
