/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-02 20:48 创建
 */
package org.antframework.idcenter.biz.service;

import org.antframework.common.util.facade.BizException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.Status;
import org.antframework.idcenter.biz.util.ProducerUtils;
import org.antframework.idcenter.dal.dao.IderDao;
import org.antframework.idcenter.dal.dao.ProducerDao;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.dal.entity.Producer;
import org.antframework.idcenter.facade.order.AcquireIdsOrder;
import org.antframework.idcenter.facade.result.AcquireIdsResult;
import org.antframework.idcenter.facade.vo.Period;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceCheck;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Random;

/**
 * 获取批量id服务
 */
@Service(enableTx = true)
public class AcquireIdsService {
    private static final Logger logger = LoggerFactory.getLogger(AcquireIdsService.class);
    // 随机
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
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", order.getIdCode()));
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
            throw new BizException(Status.FAIL, CommonResultCode.ILLEGAL_STATE.getCode(), String.format("id提供者[%s]的因数被修改，请重试", ider.getIdCode()));
        }
        // 刷新ider（生产者被锁住前因数可能会被修改，在此更新到最新因数）
        ider = iderDao.findByIdCode(ider.getIdCode());
        logger.info("生产id前：id提供者={},生产者={}", ider, producer);
        // 计算生产的id数量
        int amount = order.getExpectAmount();
        if (ider.getMaxAmount() != null && amount > ider.getMaxAmount()) {
            logger.warn("期望获取id的数量{}过多，调整到{}", amount, ider.getMaxAmount());
            amount = ider.getMaxAmount();
        }
        // 现代化生产者
        modernizeProducer(ider, producer);
        // 生产id
        result.setIdsInfos(ProducerUtils.produce(ider, producer, amount));
        // 更新生产者
        producerDao.save(producer);
        logger.info("生产id后：生产者={}", producer);
    }

    // 如果生产者的当前周期小于最新周期，则更新当前周期
    private void modernizeProducer(Ider ider, Producer producer) {
        Period modernPeriod = new Period(ider.getPeriodType(), new Date());

        Period period = new Period(ider.getPeriodType(), producer.getCurrentPeriod());
        if (period.compareTo(modernPeriod) < 0) {
            int modernStartId = calcModernStartId(ider, producer, modernPeriod);
            producer.setCurrentPeriod(modernPeriod.getDate());
            producer.setCurrentId((long) modernStartId);
            logger.info("生产者现代化后：{}", producer);
        }
    }

    // 计算生产者跳跃到最新周期时的开始id
    private int calcModernStartId(Ider ider, Producer producer, Period modernPeriod) {
        int modernStartId = (int) (producer.getCurrentId() % ider.getFactor());
        if (ider.getMaxId() == null) {
            return modernStartId;
        }
        int growPerPeriod = ider.getFactor() - (int) (ider.getMaxId() % ider.getFactor());
        Period anchorPeriod = new Period(ider.getPeriodType(), producer.getCurrentPeriod());
        while (anchorPeriod.compareTo(modernPeriod) < 0) {
            anchorPeriod = anchorPeriod.grow(1);
            modernStartId = (modernStartId + growPerPeriod) % ider.getFactor();
        }
        return modernStartId;
    }
}
