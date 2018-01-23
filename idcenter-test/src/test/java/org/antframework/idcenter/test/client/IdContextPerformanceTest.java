/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-23 11:22 创建
 */
package org.antframework.idcenter.test.client;

import org.antframework.idcenter.client.Id;
import org.antframework.idcenter.client.IdContext;
import org.antframework.idcenter.client.core.DefaultIdAcquirer;
import org.antframework.idcenter.client.core.Ids;
import org.antframework.idcenter.client.support.IdStorage;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 客户端性能测试
 */
@Ignore
public class IdContextPerformanceTest {
    // 并发线程数
    private static final int THREAD_COUNT = 10;
    // 每个任务内循环次数
    private static final int COUNT_PER_TASK = 100000000;
    // 线程池
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            THREAD_COUNT,
            THREAD_COUNT,
            5,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(1),
            new ThreadPoolExecutor.DiscardPolicy());
    // 性能结果
    private Performance[] performances = new Performance[THREAD_COUNT];
    // 主线程等待队列
    private BlockingQueue blockingQueue = new LinkedBlockingQueue();
    // id客户端
    private IdContext idContext;

    @Before
    public void init() {
        IdContext.InitParams initParams = new IdContext.InitParams();
        initParams.setIdCode("oid");
        initParams.setServerUrl("http://localhost:6210");
        initParams.setInitAmount(100);
        initParams.setMinTime(10 * 60 * 1000);
        initParams.setMaxTime(15 * 60 * 1000);

        idContext = new IdContext(initParams);
    }

    @Test
    public void testPerformance() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        for (int i = 0; i < THREAD_COUNT; i++) {
            threadPool.execute(new Task(i));
        }
        for (int i = 0; i < THREAD_COUNT; i++) {
            blockingQueue.take();
        }
        long tps = 0;
        for (int i = 0; i < THREAD_COUNT; i++) {
            System.out.println(performances[i]);
            tps += performances[i].getTps();
        }
        System.out.println("客户端总tps：" + tps);

        Thread.sleep(5000);
        checkAmount();
    }

    // 校验id余量
    private void checkAmount() throws NoSuchFieldException, IllegalAccessException {
        DefaultIdAcquirer idAcquirer = (DefaultIdAcquirer) idContext.getAcquirer();

        Field idStorageField = DefaultIdAcquirer.class.getDeclaredField("idStorage");
        ReflectionUtils.makeAccessible(idStorageField);
        IdStorage idStorage = (IdStorage) idStorageField.get(idAcquirer);

        Field amountField = IdStorage.class.getDeclaredField("amount");
        ReflectionUtils.makeAccessible(amountField);
        AtomicLong amount = (AtomicLong) amountField.get(idStorage);

        Field idsQueueField = IdStorage.class.getDeclaredField("idsQueue");
        ReflectionUtils.makeAccessible(idsQueueField);
        Queue<Ids> idsQueue = (Queue<Ids>) idsQueueField.get(idStorage);

        long realAmount = 0;
        for (Ids ids : idsQueue) {
            realAmount += ids.getAmount(null);
        }

        System.out.println("id仓库记录余量：" + amount.get() + "，id真正余量：" + realAmount);
        Assert.assertEquals(realAmount, amount.get());
    }

    // 任务
    private class Task implements Runnable {
        // 任务序号
        private int index;

        public Task(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            int count = 100000000;
            int nullCount = 0;
            for (int i = 0; i < count; i++) {
                Id id = idContext.getAcquirer().getId();
                if (id == null) {
                    nullCount++;
                }
            }
            long endTime = System.currentTimeMillis();
            performances[index] = new Performance(index, startTime, endTime, count, nullCount);

            blockingQueue.offer(new Object());
        }
    }

    // 性能结果
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

        public Performance(int index, long startTime, long endTime, int count, int nullCount) {
            this.index = index;
            this.startTime = startTime;
            this.endTime = endTime;
            this.count = count;
            this.nullCount = nullCount;
        }

        public int getIndex() {
            return index;
        }

        public long getStartTime() {
            return startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public int getCount() {
            return count;
        }

        public int getNullCount() {
            return nullCount;
        }

        public long getTimeCost() {
            return endTime - startTime;
        }

        public long getTps() {
            return (count - nullCount) * 1000L / getTimeCost();
        }

        @Override
        public String toString() {
            return String.format("任务序号：%d，开始时间：%s，结束时间：%s，循环次数：%d，id出现null次数：%d，总耗时：%d毫秒，tps：%d",
                    index,
                    DateFormatUtils.format(new Date(startTime), "yyyy-MM-dd HH:mm:ss.SSS"),
                    DateFormatUtils.format(new Date(endTime), "yyyy-MM-dd HH:mm:ss.SSS"),
                    count,
                    nullCount,
                    getTimeCost(),
                    getTps());
        }
    }
}
