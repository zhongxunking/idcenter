/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-24 21:35 创建
 */
package org.antframework.idcenter.test.facade.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.antframework.common.util.facade.FacadeUtils;
import org.antframework.idcenter.facade.api.IderService;
import org.antframework.idcenter.facade.order.AcquireIdsOrder;
import org.antframework.idcenter.facade.result.AcquireIdsResult;
import org.antframework.idcenter.test.AbstractTest;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.*;

/**
 * 获取批量id性能测试
 */
@Ignore
public class AcquireIdsPerformanceTest extends AbstractTest {
    private static final int AMOUNT_OF_TASKS = 100;
    private static final int COUNT_IN_TASK = 200;
    private Executor executor = new ThreadPoolExecutor(
            AMOUNT_OF_TASKS,
            AMOUNT_OF_TASKS,
            5,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(AMOUNT_OF_TASKS));
    private CountDownLatch latch = new CountDownLatch(AMOUNT_OF_TASKS);
    private Performance[] performances = new Performance[AMOUNT_OF_TASKS];
    @Autowired
    private IderService iderService;

    @Test
    public void testAcquireIds() throws InterruptedException {
        acquireIds();
        for (int i = 0; i < AMOUNT_OF_TASKS; i++) {
            executor.execute(new Task(i));
        }
        latch.await();

        long totalCount = 0;
        double totalTps = 0;
        for (Performance performance : performances) {
            System.out.println(performance);
            totalCount += performance.getCount();
            totalTps += performance.getTps();
        }
        System.out.println("-------------------汇总------------------");
        System.out.println(String.format("总调用次数：%d，总tps：%.2f", totalCount, totalTps));
    }

    @AllArgsConstructor
    private class Task implements Runnable {
        // 任务序号
        private final int index;

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < COUNT_IN_TASK; i++) {
                acquireIds();
            }
            long endTime = System.currentTimeMillis();
            performances[index] = new Performance(index, startTime, endTime, COUNT_IN_TASK);
            latch.countDown();
        }
    }

    private void acquireIds() {
        AcquireIdsOrder order = new AcquireIdsOrder();
        order.setIderId("userId");
        order.setExpectAmount(100);

        AcquireIdsResult result = iderService.acquireIds(order);
        FacadeUtils.assertSuccess(result);
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

        public long getTimeCost() {
            return endTime - startTime;
        }

        public double getTps() {
            return count * 1000D / getTimeCost();
        }

        @Override
        public String toString() {
            return String.format("任务序号：%d，开始时间：%s，结束时间：%s，耗时：%d毫秒，调用次数：%d，tps：%.2f",
                    index,
                    DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm:ss.SSS"),
                    DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm:ss.SSS"),
                    getTimeCost(),
                    count,
                    getTps());
        }
    }
}
