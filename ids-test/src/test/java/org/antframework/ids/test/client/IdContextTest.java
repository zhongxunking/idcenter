/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-03 00:19 创建
 */
package org.antframework.ids.test.client;

import org.antframework.ids.client.Id;
import org.antframework.ids.client.IdAcquirer;
import org.antframework.ids.client.IdContext;
import org.junit.Ignore;
import org.junit.Test;

/**
 * id上下文单元测试
 */
@Ignore
public class IdContextTest {

    @Test
    public void testIdContext() {
        IdContext.InitParams initParams = new IdContext.InitParams();
        initParams.setIdCode("oid");
        initParams.setServerUrl("http://localhost:6210");
        initParams.setInitAmount(100);
        initParams.setMaxTime(15 * 60 * 1000);
        initParams.setMinTime(10 * 60 * 1000);

        IdContext idContext = new IdContext(initParams);
        IdAcquirer idAcquirer = idContext.getAcquirer();
        Id id = idAcquirer.getId();
    }
}
