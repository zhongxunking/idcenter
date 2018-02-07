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

/**
 * id服务单元测试
 */
@Ignore
public class IdServiceTest extends AbstractTest {
    @Autowired
    private IdService idService;

    @Test
    public void testAcquireIds() throws InterruptedException {
        AcquireIdsOrder order = new AcquireIdsOrder();
        order.setIdCode("uid");
        order.setExpectAmount(1000);
        AcquireIdsResult result = idService.acquireIds(order);
        assertSuccess(result);
    }
}
