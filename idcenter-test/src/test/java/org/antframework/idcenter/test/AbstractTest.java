/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-08-22 15:42 创建
 */
package org.antframework.idcenter.test;

import org.antframework.boot.lang.Apps;
import org.antframework.common.util.facade.AbstractResult;
import org.antframework.common.util.facade.Status;
import org.antframework.idcenter.Main;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 单元测试父类
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
public class AbstractTest {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractTest.class);

    static {
        // 设置使用环境
        Apps.setProfileIfAbsent("dev");
    }

    protected void assertSuccess(AbstractResult result) {
        Assert.assertEquals(Status.SUCCESS, result.getStatus());
    }
}
