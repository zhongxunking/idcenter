/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-02 13:33 创建
 */
package org.antframework.idcenter.biz.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antframework.common.util.facade.BizException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.EmptyResult;
import org.antframework.common.util.facade.Status;
import org.antframework.common.util.id.Period;
import org.antframework.idcenter.dal.dao.IdProducerDao;
import org.antframework.idcenter.dal.dao.IderDao;
import org.antframework.idcenter.dal.entity.IdProducer;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.facade.order.AddIderOrder;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * 新增id提供者服务
 */
@Service(enableTx = true)
@AllArgsConstructor
@Slf4j
public class AddIderService {
    // id提供者dao
    private final IderDao iderDao;
    // id生产者dao
    private final IdProducerDao idProducerDao;

    @ServiceExecute
    public void execute(ServiceContext<AddIderOrder, EmptyResult> context) {
        AddIderOrder order = context.getOrder();

        Ider ider = iderDao.findLockByIderId(order.getIderId());
        if (ider != null) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]已存在", order.getIderId()));
        }

        ider = buildIder(order);
        iderDao.save(ider);
        log.info("新增id提供者：{}", ider);

        IdProducer idProducer = buildIdProducer(ider);
        idProducerDao.save(idProducer);
        log.info("新增id生产者：{}", idProducer);
    }

    // 构建id提供者
    private Ider buildIder(AddIderOrder addIderOrder) {
        Ider ider = new Ider();
        BeanUtils.copyProperties(addIderOrder, ider);
        ider.setFactor(1);

        return ider;
    }

    // 构建id生产者
    private IdProducer buildIdProducer(Ider ider) {
        Period period = new Period(ider.getPeriodType(), new Date());

        IdProducer idProducer = new IdProducer();
        idProducer.setIderId(ider.getIderId());
        idProducer.setIndex(0);
        idProducer.setCurrentPeriod(period.getDate());
        idProducer.setCurrentId(0L);

        return idProducer;
    }
}
