/*
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-23 11:22 创建
 */
package org.antframework.idcenter.test.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.antframework.common.util.id.Id;
import org.antframework.idcenter.client.Ider;
import org.antframework.idcenter.client.IderContext;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 客户端性能测试
 */
@Ignore
@Slf4j
public class IderContextPerformanceTest {
    // 并发线程数
    private static final int AMOUNT_OF_THREAD = 10;
    // 每个任务内循环次数
    private static final int COUNT_IN_THREAD = 100000;
    // 线程池
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            AMOUNT_OF_THREAD,
            AMOUNT_OF_THREAD,
            5,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(1),
            new ThreadPoolExecutor.DiscardPolicy());
    // 性能结果
    private Performance[] performances = new Performance[AMOUNT_OF_THREAD];
    // 计数闩
    private CountDownLatch latch = new CountDownLatch(AMOUNT_OF_THREAD);
    // id提供者
    private Ider ider;

    @Before
    public void init() {
        IderContext iderContext = new IderContext("http://localhost:6210", 10 * 60 * 1000, 15 * 60 * 1000, null);
        ider = iderContext.getIder("userId");
    }

    @Test
    public void testPerformance() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        for (int i = 0; i < AMOUNT_OF_THREAD; i++) {
            threadPool.execute(new Task(i));
        }
        latch.await();
        double tps = 0;
        int nullCount = 0;
        for (Performance performance : performances) {
            log.info("单任务性能：{}", performance);
            tps += performance.getTps();
            nullCount += performance.getNullCount();
        }
        log.info("客户端性能：总循环次数={}，id出现null总次数={}，总tps={}", AMOUNT_OF_THREAD * COUNT_IN_THREAD, nullCount, tps);
    }

    // 任务
    @AllArgsConstructor
    private class Task implements Runnable {
        // 任务序号
        private int index;

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            int nullCount = 0;
            for (int i = 0; i < COUNT_IN_THREAD; i++) {
                Id id = ider.acquire();
                if (id == null) {
                    nullCount++;
                }
            }
            long endTime = System.currentTimeMillis();
            performances[index] = new Performance(index, startTime, endTime, COUNT_IN_THREAD, nullCount);
            latch.countDown();
        }
    }

    // 性能结果
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

        public long getTimeCost() {
            return endTime - startTime;
        }

        public double getTps() {
            return (count - nullCount) * 1000D / getTimeCost();
        }

        @Override
        public String toString() {
            return String.format("任务序号=%d，开始时间=%s，结束时间=%s，循环次数=%d，id出现null次数=%d，总耗时：%d毫秒，tps=%.2f",
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
