/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-02 20:48 创建
 */
package org.antframework.ids.biz.service;

import org.antframework.boot.bekit.AntBekitException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.Status;
import org.antframework.ids.biz.util.ProducerUtils;
import org.antframework.ids.dal.dao.IderDao;
import org.antframework.ids.dal.dao.ProducerDao;
import org.antframework.ids.dal.entity.Ider;
import org.antframework.ids.dal.entity.Producer;
import org.antframework.ids.facade.order.AcquireIdsOrder;
import org.antframework.ids.facade.result.AcquireIdsResult;
import org.antframework.ids.facade.util.PeriodUtils;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceCheck;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Random;

/**
 * 获取批量id服务
 */
@Service(enableTx = true)
public class AcquireIdsService {
    private static final Random RANDOM = new Random();
    @Autowired
    private IderDao iderDao;
    @Autowired
    private ProducerDao producerDao;

    @ServiceCheck
    public void check(ServiceContext<AcquireIdsOrder, AcquireIdsResult> context) {
        AcquireIdsOrder order = context.getOrder();

        Ider ider = iderDao.findByIdCode(order.getIdCode());
        if (ider == null) {
            throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", order.getIdCode()));
        }
        context.setAttachmentAttr(Ider.class, ider);
    }

    @ServiceExecute
    public void execute(ServiceContext<AcquireIdsOrder, AcquireIdsResult> context) {
        AcquireIdsOrder order = context.getOrder();
        AcquireIdsResult result = context.getResult();
        Ider ider = context.getAttachmentAttr(Ider.class);

        Producer producer = producerDao.findLockByIdCodeAndIndex(ider.getIdCode(), RANDOM.nextInt(ider.getFactor()));
        if (producer == null) {
            throw new AntBekitException(Status.FAIL, CommonResultCode.ILLEGAL_STATE.getCode(), String.format("id提供者[%s]的因数被修改，请重试", ider.getIdCode()));
        }
        // 刷新ider（生产者被锁住前因数可能会被修改，在此更新到最新因数）
        ider = iderDao.findByIdCode(ider.getIdCode());
        modernizeProducer(ider, producer);
        result.setIdsInfos(ProducerUtils.produce(ider, producer, order.getExpectAmount()));
        producerDao.save(producer);
    }

    private void modernizeProducer(Ider ider, Producer producer) {
        Date modernPeriod = PeriodUtils.parse(ider.getPeriodType(), new Date());
        if (PeriodUtils.compare(ider.getPeriodType(), modernPeriod, producer.getCurrentPeriod()) > 0) {
            producer.setCurrentPeriod(modernPeriod);
            producer.setCurrentId((long) calcModernStartId(ider, producer, modernPeriod));
        }
    }

    private int calcModernStartId(Ider ider, Producer producer, Date modernPeriod) {
        int modernStartId = (int) (producer.getCurrentId() % ider.getFactor());
        if (ider.getMaxId() == null) {
            return modernStartId;
        }
        int growPerPeriod = ider.getFactor() - (int) (ider.getMaxId() % ider.getFactor());
        Date anchorPeriod = producer.getCurrentPeriod();
        while (anchorPeriod.getTime() < modernPeriod.getTime()) {
            anchorPeriod = PeriodUtils.grow(ider.getPeriodType(), anchorPeriod, 1);
            modernStartId = (modernStartId + growPerPeriod) % ider.getFactor();
        }
        return modernStartId;
    }
}
