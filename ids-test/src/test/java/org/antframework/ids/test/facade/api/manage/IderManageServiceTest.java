/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-22 23:20 创建
 */
package org.antframework.ids.test.facade.api.manage;

import org.antframework.ids.facade.api.manage.IderManageService;
import org.antframework.ids.facade.order.DeleteIderOrder;
import org.antframework.ids.facade.order.ModifyIderCurrentOrder;
import org.antframework.ids.facade.order.ModifyIderFactorOrder;
import org.antframework.ids.facade.order.QueryIderOrder;
import org.antframework.ids.facade.result.DeleteIderResult;
import org.antframework.ids.facade.result.ModifyIderCurrentResult;
import org.antframework.ids.facade.result.ModifyIderFactorResult;
import org.antframework.ids.facade.result.QueryIderResult;
import org.antframework.ids.test.AbstractTest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * id提供者管理服务单元测试
 */
@Ignore
public class IderManageServiceTest extends AbstractTest {
    @Autowired
    private IderManageService iderManageService;

    @Test
    public void testModifyIderFactor() {
        ModifyIderFactorOrder order = new ModifyIderFactorOrder();
        order.setIdCode("oid");
        order.setNewFactor(4);

        ModifyIderFactorResult result = iderManageService.modifyIderFactor(order);
        assertSuccess(result);
    }

    @Test
    public void testModifyIderCurrent() {
        ModifyIderCurrentOrder order = new ModifyIderCurrentOrder();
        order.setIdCode("oid");
        order.setNewCurrentPeriod(new Date());
        order.setNewCurrentId(100);

        ModifyIderCurrentResult result = iderManageService.modifyIderCurrent(order);
        assertSuccess(result);
    }

    @Test
    public void testDeleteIder() {
        DeleteIderOrder order = new DeleteIderOrder();
        order.setIdCode("oid");

        DeleteIderResult result = iderManageService.deleteIder(order);
        assertSuccess(result);
    }

    @Test
    public void testQueryIder() {
        QueryIderOrder order = new QueryIderOrder();
        order.setPageNo(1);
        order.setPageSize(10);
        order.setIdCode("o");

        QueryIderResult result = iderManageService.queryIder(order);
        assertSuccess(result);
    }
}
