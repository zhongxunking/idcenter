/*
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-03 00:19 创建
 */
package org.antframework.idcenter.test.client;

import lombok.extern.slf4j.Slf4j;
import org.antframework.idcenter.client.Ider;
import org.antframework.idcenter.client.IderContext;
import org.antframework.idcenter.client.core.DefaultIder;
import org.junit.Ignore;
import org.junit.Test;

/**
 * id提供者上下文单元测试
 */
@Ignore
@Slf4j
public class IderContextTest {
    @Test
    public void testSingleIderSingleThread() {
        IderContext iderContext = new IderContext("http://localhost:6210", 10 * 60 * 1000, 15 * 60 * 1000, null);
        Ider ider = iderContext.getIder("userId");
        log.info("-----------单ider单线程-----------start");
        new SingleIderSingleThreadTask(0, ider, 1000000, performance -> {
        }, false).run();
        IderChecker.checkIdAmount((DefaultIder) ider);
        log.info("-----------单ider单线程-----------end");
    }

    @Test
    public void testSingleIderMultiThread() {
        log.info("-----------单ider多线程-----------start");
        new SingleIderMultiThreadTask(0, "http://localhost:6210", "userId", 10, 100000, performance -> {
        }, false).run();
        log.info("-----------单ider多线程-----------end");
    }

    @Test
    public void testMultiIderMultiThread() {
        log.info("------------多ider多线程-------------start");
        new MultiIderMultiThreadTask(0, "http://localhost:6210", "userId", 10, 5, 20000, performance -> {
        }, false).run();
        log.info("------------多ider多线程-------------end");
    }
}
