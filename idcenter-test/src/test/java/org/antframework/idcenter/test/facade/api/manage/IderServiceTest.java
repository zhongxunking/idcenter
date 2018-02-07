/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-22 23:20 创建
 */
package org.antframework.idcenter.test.facade.api.manage;

import org.antframework.common.util.facade.EmptyResult;
import org.antframework.idcenter.facade.api.manage.IderService;
import org.antframework.idcenter.facade.enums.PeriodType;
import org.antframework.idcenter.facade.order.*;
import org.antframework.idcenter.facade.result.FindIderResult;
import org.antframework.idcenter.facade.result.QueryIderResult;
import org.antframework.idcenter.test.AbstractTest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * id提供者服务单元测试
 */
@Ignore
public class IderServiceTest extends AbstractTest {
    @Autowired
    private IderService iderService;

    @Test
    public void testAddIder() {
        AddIderOrder order = new AddIderOrder();
        order.setIdCode("uid");
        order.setPeriodType(PeriodType.HOUR);
        order.setMaxId(9000000000L);
        order.setMaxAmount(1000000);

        EmptyResult result = iderService.addIder(order);
        assertSuccess(result);
    }

    @Test
    public void testModifyIderMax() {
        ModifyIderMaxOrder order = new ModifyIderMaxOrder();
        order.setIdCode("uid");
        order.setNewMaxId(9000000000L - 10 * 4);
        order.setNewMaxAmount(2000000);

        EmptyResult result = iderService.modifyIderMax(order);
        assertSuccess(result);
    }

    @Test
    public void testModifyIderFactor() {
        ModifyIderFactorOrder order = new ModifyIderFactorOrder();
        order.setIdCode("uid");
        order.setNewFactor(4);

        EmptyResult result = iderService.modifyIderFactor(order);
        assertSuccess(result);
    }

    @Test
    public void testModifyIderCurrent() {
        ModifyIderCurrentOrder order = new ModifyIderCurrentOrder();
        order.setIdCode("uid");
        order.setNewCurrentPeriod(new Date());
        order.setNewCurrentId(100);

        EmptyResult result = iderService.modifyIderCurrent(order);
        assertSuccess(result);
    }

    @Test
    public void testDeleteIder() {
        DeleteIderOrder order = new DeleteIderOrder();
        order.setIdCode("uid");

        EmptyResult result = iderService.deleteIder(order);
        assertSuccess(result);
    }

    @Test
    public void testFindIder() {
        FindIderOrder order = new FindIderOrder();
        order.setIdCode("uid");

        FindIderResult result = iderService.findIder(order);
        assertSuccess(result);
    }

    @Test
    public void testQueryIder() {
        QueryIderOrder order = new QueryIderOrder();
        order.setPageNo(1);
        order.setPageSize(10);

        QueryIderResult result = iderService.queryIder(order);
        assertSuccess(result);
    }
}
