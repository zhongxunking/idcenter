/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-27 23:19 创建
 */
package org.antframework.ids.biz.service;

import org.antframework.boot.bekit.AntBekitException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.Status;
import org.antframework.ids.dal.dao.IderDao;
import org.antframework.ids.dal.dao.ProducerDao;
import org.antframework.ids.dal.entity.Ider;
import org.antframework.ids.dal.entity.Producer;
import org.antframework.ids.facade.enums.PeriodType;
import org.antframework.ids.facade.order.ModifyIderCurrentOrder;
import org.antframework.ids.facade.result.ModifyIderCurrentResult;
import org.antframework.ids.facade.util.PeriodUtils;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 修改id提供者当前数据服务
 */
@Service(enableTx = true)
public class ModifyIderCurrentService {
    @Autowired
    private IderDao iderDao;
    @Autowired
    private ProducerDao producerDao;

    @ServiceExecute
    public void execute(ServiceContext<ModifyIderCurrentOrder, ModifyIderCurrentResult> context) {
        ModifyIderCurrentOrder order = context.getOrder();

        Ider ider = iderDao.findLockByIdCode(order.getIdCode());
        if (ider == null) {
            throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", order.getIdCode()));
        }
        if (ider.getPeriodType() != PeriodType.NONE && order.getNewCurrentPeriod() == null) {
            throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]周期类型为%s，新的当前周期不能为null", ider.getIdCode(), ider.getPeriodType()));
        }

        List<Producer> producers = producerDao.findLockByIdCodeOrderByIndexAsc(ider.getIdCode());
        for (Producer producer : producers) {
            producer.setCurrentPeriod(PeriodUtils.parsePeriod(ider.getPeriodType(), order.getNewCurrentPeriod()));
            producer.setCurrentId(order.getNewCurrentId());

            producerDao.save(producer);
        }
    }
}
