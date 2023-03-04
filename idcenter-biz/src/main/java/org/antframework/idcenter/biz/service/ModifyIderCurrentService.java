/*
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-27 23:19 创建
 */
package org.antframework.idcenter.biz.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antframework.common.util.facade.BizException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.EmptyResult;
import org.antframework.common.util.facade.Status;
import org.antframework.common.util.id.Period;
import org.antframework.common.util.id.PeriodType;
import org.antframework.idcenter.dal.dao.IderDao;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.facade.order.ModifyIderCurrentOrder;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;

/**
 * 修改id提供者当前数据服务
 */
@Service(enableTx = true)
@AllArgsConstructor
@Slf4j
public class ModifyIderCurrentService {
    // id提供者dao
    private final IderDao iderDao;

    @ServiceExecute
    public void execute(ServiceContext<ModifyIderCurrentOrder, EmptyResult> context) {
        ModifyIderCurrentOrder order = context.getOrder();

        Ider ider = iderDao.findLockByIderId(order.getIderId());
        if (ider == null) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", order.getIderId()));
        }
        if (ider.getPeriodType() != PeriodType.NONE && order.getNewCurrentPeriod() == null) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]周期类型为%s，新的当前周期不能为null", ider.getIderId(), ider.getPeriodType()));
        }
        if (ider.getMaxId() != null && order.getNewCurrentId() >= ider.getMaxId()) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("新的当前id[%d]超过id提供者[%s]允许的最大值[%d]（不包含）", order.getNewCurrentId(), ider.getIderId(), ider.getMaxId()));
        }

        log.info("被修改当前数据前的id提供者：{}", ider);
        Period newPeriod = new Period(ider.getPeriodType(), order.getNewCurrentPeriod());
        ider.setCurrentPeriod(newPeriod.getDate());
        ider.setCurrentId(order.getNewCurrentId());
        iderDao.save(ider);
        log.info("被修改当前数据后的id提供者：{}", ider);
    }
}
