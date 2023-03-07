/*
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-22 23:20 创建
 */
package org.antframework.idcenter.test.facade.api;

import org.antframework.common.util.facade.EmptyResult;
import org.antframework.common.util.id.PeriodType;
import org.antframework.datasource.DataSourceTemplate;
import org.antframework.idcenter.facade.api.IderService;
import org.antframework.idcenter.facade.order.*;
import org.antframework.idcenter.facade.result.AcquireIdsResult;
import org.antframework.idcenter.facade.result.FindIderResult;
import org.antframework.idcenter.facade.result.QueryIdersResult;
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
    // 数据源
    private final String dataSource = "db0";
    @Autowired
    private DataSourceTemplate dataSourceTemplate;
    @Autowired
    private IderService iderService;

    @Test
    public void testAddIder() {
        AddIderOrder order = new AddIderOrder();
        order.setIderId("userId");
        order.setIderName("用户id");
        order.setPeriodType(PeriodType.HOUR);
        order.setMaxId(9000000000L);
        order.setMaxAmount(1000000);

        EmptyResult result = dataSourceTemplate.doWith(dataSource, () -> iderService.addIder(order));
        assertSuccess(result);
    }

    @Test
    public void testModifyIderName() {
        ModifyIderNameOrder order = new ModifyIderNameOrder();
        order.setIderId("userId");
        order.setNewIderName("用户id2");

        EmptyResult result = dataSourceTemplate.doWith(dataSource, () -> iderService.modifyIderName(order));
        assertSuccess(result);
    }

    @Test
    public void testModifyIderMax() {
        ModifyIderMaxOrder order = new ModifyIderMaxOrder();
        order.setIderId("userId");
        order.setNewMaxId(9000000000L - 100 * 4);
        order.setNewMaxAmount(2000000);

        EmptyResult result = dataSourceTemplate.doWith(dataSource, () -> iderService.modifyIderMax(order));
        assertSuccess(result);
    }

    @Test
    public void testModifyIderCurrent() {
        ModifyIderCurrentOrder order = new ModifyIderCurrentOrder();
        order.setIderId("userId");
        order.setNewCurrentPeriod(new Date());
        order.setNewCurrentId(100L);

        EmptyResult result = dataSourceTemplate.doWith(dataSource, () -> iderService.modifyIderCurrent(order));
        assertSuccess(result);
    }

    @Test
    public void testDeleteIder() {
        DeleteIderOrder order = new DeleteIderOrder();
        order.setIderId("userId");

        EmptyResult result = dataSourceTemplate.doWith(dataSource, () -> iderService.deleteIder(order));
        assertSuccess(result);
    }

    @Test
    public void testAcquireIds() {
        AcquireIdsOrder order = new AcquireIdsOrder();
        order.setIderId("userId");
        order.setAmount(1000);

        AcquireIdsResult result = dataSourceTemplate.doWith(dataSource, () -> iderService.acquireIds(order));
        assertSuccess(result);
    }

    @Test
    public void testFindIder() {
        FindIderOrder order = new FindIderOrder();
        order.setIderId("userId");

        FindIderResult result = dataSourceTemplate.doWith(dataSource, () -> iderService.findIder(order));
        assertSuccess(result);
    }

    @Test
    public void testQueryIders() {
        QueryIdersOrder order = new QueryIdersOrder();
        order.setPageNo(1);
        order.setPageSize(10);

        QueryIdersResult result = dataSourceTemplate.doWith(dataSource, () -> iderService.queryIders(order));
        assertSuccess(result);
    }
}
