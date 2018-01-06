/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-02 13:33 创建
 */
package org.antframework.idcenter.biz.service;

import org.antframework.common.util.facade.BizException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.EmptyResult;
import org.antframework.common.util.facade.Status;
import org.antframework.idcenter.dal.dao.IderDao;
import org.antframework.idcenter.dal.dao.ProducerDao;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.dal.entity.Producer;
import org.antframework.idcenter.facade.order.AddIderOrder;
import org.antframework.idcenter.facade.vo.Period;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * 新增id提供者服务
 */
@Service(enableTx = true)
public class AddIderService {
    @Autowired
    private IderDao iderDao;
    @Autowired
    private ProducerDao producerDao;

    @ServiceExecute
    public void execute(ServiceContext<AddIderOrder, EmptyResult> context) {
        AddIderOrder order = context.getOrder();

        Ider ider = iderDao.findLockByIdCode(order.getIdCode());
        if (ider != null) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]已存在", order.getIdCode()));
        }
        ider = buildIder(order);
        producerDao.save(buildProducer(ider));
        iderDao.save(ider);
    }

    // 构建id提供者
    private Ider buildIder(AddIderOrder addIderOrder) {
        Ider ider = new Ider();
        BeanUtils.copyProperties(addIderOrder, ider);
        ider.setFactor(1);

        return ider;
    }

    // 构建生产者
    private Producer buildProducer(Ider ider) {
        Period period = new Period(ider.getPeriodType(), new Date());

        Producer producer = new Producer();
        producer.setIdCode(ider.getIdCode());
        producer.setIndex(0);
        producer.setCurrentPeriod(period.getDate());
        producer.setCurrentId(0L);

        return producer;
    }
}
