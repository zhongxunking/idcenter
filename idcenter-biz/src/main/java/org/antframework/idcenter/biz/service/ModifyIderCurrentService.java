/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-27 23:19 创建
 */
package org.antframework.idcenter.biz.service;

import org.antframework.boot.bekit.AntBekitException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.EmptyResult;
import org.antframework.common.util.facade.Status;
import org.antframework.idcenter.biz.util.ProducerUtils;
import org.antframework.idcenter.dal.dao.IderDao;
import org.antframework.idcenter.dal.dao.ProducerDao;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.dal.entity.Producer;
import org.antframework.idcenter.facade.enums.PeriodType;
import org.antframework.idcenter.facade.order.ModifyIderCurrentOrder;
import org.antframework.idcenter.facade.util.PeriodUtils;
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
    public void execute(ServiceContext<ModifyIderCurrentOrder, EmptyResult> context) {
        ModifyIderCurrentOrder order = context.getOrder();

        Ider ider = iderDao.findLockByIdCode(order.getIdCode());
        if (ider == null) {
            throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", order.getIdCode()));
        }
        if (ider.getPeriodType() != PeriodType.NONE && order.getNewCurrentPeriod() == null) {
            throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]周期类型为%s，新的当前周期不能为null", ider.getIdCode(), ider.getPeriodType()));
        }
        if (ider.getMaxId() != null && order.getNewCurrentId() >= ider.getMaxId()) {
            throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("新的当前id[%d]超过id提供者[%s]允许的最大值[%d]（不包含）", order.getNewCurrentId(), ider.getIdCode(), ider.getMaxId()));
        }

        List<Producer> producers = producerDao.findLockByIdCodeOrderByIndexAsc(ider.getIdCode());
        for (int i = 0; i < ider.getFactor(); i++) {
            Producer producer = producers.get(i);
            producer.setCurrentPeriod(PeriodUtils.parse(ider.getPeriodType(), order.getNewCurrentPeriod()));
            producer.setCurrentId(order.getNewCurrentId());
            ProducerUtils.grow(ider, producer, i);

            producerDao.save(producer);
        }
    }
}
