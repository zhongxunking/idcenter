/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-03 00:19 创建
 */
package org.antframework.idcenter.test.client;

import org.antframework.idcenter.client.Id;
import org.antframework.idcenter.client.IdAcquirer;
import org.antframework.idcenter.client.IdContext;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * id上下文单元测试
 */
@Ignore
public class IdContextTest {
    private static final Logger logger = LoggerFactory.getLogger(IdContextTest.class);

    @Test
    public void testIdContext() throws InterruptedException {
        IdContext.InitParams initParams = new IdContext.InitParams();
        initParams.setIdCode("oid");
        initParams.setServerUrl("http://localhost:6210");
        initParams.setInitAmount(100);
        initParams.setMaxTime(15 * 60 * 1000);
        initParams.setMinTime(10 * 60 * 1000);

        IdContext idContext = new IdContext(initParams);
        IdAcquirer idAcquirer = idContext.getAcquirer();
        for (int i = 0; i < 20000; i++) {
            Id id = idAcquirer.getId();
            Thread.sleep(20);
            logger.info("----{}----", i);
        }
        int a = 0;
    }
}
