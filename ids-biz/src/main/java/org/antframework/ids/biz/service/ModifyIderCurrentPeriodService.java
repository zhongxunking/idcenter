/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-23 22:11 创建
 */
package org.antframework.ids.biz.service;

import org.antframework.boot.bekit.AntBekitException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.Status;
import org.antframework.ids.dal.dao.IderDao;
import org.antframework.ids.dal.entity.Ider;
import org.antframework.ids.facade.order.ModifyIderCurrentPeriodOrder;
import org.antframework.ids.facade.result.ModifyIderCurrentPeriodResult;
import org.antframework.ids.facade.util.PeriodUtils;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 修改id提供者当前周期
 */
@Service(enableTx = true)
public class ModifyIderCurrentPeriodService {
    @Autowired
    private IderDao iderDao;

    @ServiceExecute
    public void execute(ServiceContext<ModifyIderCurrentPeriodOrder, ModifyIderCurrentPeriodResult> context) {
        ModifyIderCurrentPeriodOrder order = context.getOrder();

        Ider ider = iderDao.findLockByIdCode(order.getIdCode());
        if (ider == null) {
            throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", order.getIdCode()));
        }
        ider.setCurrentPeriod(PeriodUtils.parsePeriod(ider.getPeriodType(), order.getNewCurrentPeriod()));
        iderDao.save(ider);
    }
}
