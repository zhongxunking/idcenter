/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-03 01:15 创建
 */
package org.antframework.ids.test.facade.api;

import org.antframework.ids.facade.api.IderService;
import org.antframework.ids.facade.order.AcquireIdsOrder;
import org.antframework.ids.facade.result.AcquireIdsResult;
import org.antframework.ids.test.AbstractTest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * id提供者服务单元测试
 */
@Ignore
public class IderServiceTest extends AbstractTest {
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            1000,
            1000,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(count));
    @Autowired
    private IderService iderService;
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
            order.setIdCode("oid");
            order.setExpectAmount(100);

            AcquireIdsResult result = iderService.acquireIds(order);
            assertSuccess(result);
            if (i == count - 1) {
                long endTime = System.currentTimeMillis();
                System.out.println(String.format("耗时：%d毫秒", endTime - startTime));
            }
        }
    }
}
