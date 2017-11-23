/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-22 23:20 创建
 */
package org.antframework.ids.test.facade.api.manage;

import org.antframework.ids.facade.api.manage.IderManageService;
import org.antframework.ids.facade.enums.PeriodType;
import org.antframework.ids.facade.order.AddOrModifyIderOrder;
import org.antframework.ids.facade.order.ModifyIderCurrentIdOrder;
import org.antframework.ids.facade.order.ModifyIderCurrentPeriodOrder;
import org.antframework.ids.facade.result.AddOrModifyIderResult;
import org.antframework.ids.facade.result.ModifyIderCurrentIdResult;
import org.antframework.ids.facade.result.ModifyIderCurrentPeriodResult;
import org.antframework.ids.test.AbstractTest;
import org.apache.commons.lang3.time.DateUtils;
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
    public void testAddIder() {
        AddOrModifyIderOrder order = new AddOrModifyIderOrder();
        order.setIdCode("oid");
        order.setPeriodType(PeriodType.HOUR);
        order.setMaxId(9000000000L);

        AddOrModifyIderResult result = iderManageService.addOrModifyIder(order);
        assertSuccess(result);
    }

    @Test
    public void testModifyIderCurrentPeriod() {
        ModifyIderCurrentPeriodOrder order = new ModifyIderCurrentPeriodOrder();
        order.setIdCode("oid");
        order.setNewCurrentPeriod(DateUtils.addHours(new Date(), 2));

        ModifyIderCurrentPeriodResult result = iderManageService.modifyIderCurrentPeriod(order);
        assertSuccess(result);
    }

    @Test
    public void testModifyIderCurrentId() {
        ModifyIderCurrentIdOrder order = new ModifyIderCurrentIdOrder();
        order.setIdCode("oid");
        order.setNewCurrentId(30);

        ModifyIderCurrentIdResult result = iderManageService.modifyIderCurrentId(order);
        assertSuccess(result);
    }
}
