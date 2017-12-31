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

/**
 * id提供者服务单元测试
 */
@Ignore
public class IderServiceTest extends AbstractTest {
    @Autowired
    private IderService iderService;

    @Test
    public void testAcquireIds() throws InterruptedException {
        AcquireIdsOrder order = new AcquireIdsOrder();
        order.setIdCode("oid");
        order.setExpectAmount(1000);
        AcquireIdsResult result = iderService.acquireIds(order);
        assertSuccess(result);
    }
}
