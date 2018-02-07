/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-03 01:15 创建
 */
package org.antframework.idcenter.test.facade.api;

import org.antframework.idcenter.facade.api.IdService;
import org.antframework.idcenter.facade.order.AcquireIdsOrder;
import org.antframework.idcenter.facade.result.AcquireIdsResult;
import org.antframework.idcenter.test.AbstractTest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * id服务单元测试
 */
@Ignore
public class IdServicePerformanceTest extends AbstractTest {
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            1000,
            1000,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(count));
    @Autowired
    private IdService idService;
    private static int count = 10000;

    @Test
    public void testAcquireIds() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            threadPool.execute(new Task(startTime, i));
        }
        long endTime = System.currentTimeMillis();

        System.out.println(String.format("添加任务耗时：%d毫秒", endTime - startTime));

        Thread.sleep(1000000);
    }

    public class Task implements Runnable {
        private long startTime;
        private int i;

        public Task(long startTime, int i) {
            this.startTime = startTime;
            this.i = i;
        }

        @Override
        public void run() {
            AcquireIdsOrder order = new AcquireIdsOrder();
            order.setIdCode("uid");
            order.setExpectAmount(100);

            AcquireIdsResult result = idService.acquireIds(order);
            assertSuccess(result);
            if (i == count - 1) {
                long endTime = System.currentTimeMillis();
                System.out.println(String.format("耗时：%d毫秒", endTime - startTime));
            }
        }
    }
}
