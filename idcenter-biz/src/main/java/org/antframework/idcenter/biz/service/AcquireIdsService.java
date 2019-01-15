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
import org.antframework.common.util.id.Period;
import org.antframework.idcenter.biz.util.ProducerUtils;
import org.antframework.idcenter.dal.dao.IdProducerDao;
import org.antframework.idcenter.dal.dao.IderDao;
import org.antframework.idcenter.dal.entity.IdProducer;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.facade.order.AcquireIdsOrder;
import org.antframework.idcenter.facade.result.AcquireIdsResult;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceBefore;
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
    // 随机数生成器
    private static final Random RANDOM = new Random();
    @Autowired
    private IderDao iderDao;
    @Autowired
    private IdProducerDao idProducerDao;

    @ServiceBefore
    public void before(ServiceContext<AcquireIdsOrder, AcquireIdsResult> context) {
        AcquireIdsOrder order = context.getOrder();

        Ider ider = iderDao.findByIderId(order.getIderId());
        if (ider == null) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", order.getIderId()));
        }
        context.setAttachmentAttr(Ider.class, ider);
    }

    @ServiceExecute
    public void execute(ServiceContext<AcquireIdsOrder, AcquireIdsResult> context) {
        AcquireIdsOrder order = context.getOrder();
        AcquireIdsResult result = context.getResult();
        Ider ider = context.getAttachmentAttr(Ider.class);
        // 随机选择一个id生产者
        IdProducer idProducer = idProducerDao.findLockByIderIdAndIndex(ider.getIderId(), RANDOM.nextInt(ider.getFactor()));
        if (idProducer == null) {
            throw new BizException(Status.FAIL, CommonResultCode.ILLEGAL_STATE.getCode(), String.format("id提供者[%s]的因数被修改，请重试", ider.getIderId()));
        }
        // 刷新ider（id生产者被锁住前因数可能会被修改，在此更新到最新因数）
        ider = iderDao.findByIderId(ider.getIderId());
        logger.info("被获取id的id提供者：{}", ider);
        logger.info("生产id前的id生产者：{}", idProducer);
        // 计算生产的id数量
        int amount = order.getExpectAmount();
        if (ider.getMaxAmount() != null && amount > ider.getMaxAmount()) {
            logger.warn("期望获取id的数量[{}]过多，调整到[{}]", amount, ider.getMaxAmount());
            amount = ider.getMaxAmount();
        }
        // 现代化id生产者
        modernizeIdProducer(ider, idProducer);
        // 生产id
        result.setIdses(ProducerUtils.produce(ider, idProducer, amount));
        // 更新id生产者
        idProducerDao.save(idProducer);
        logger.info("生产id后的id生产者：{}", idProducer);
    }

    // 如果id生产者的当前周期小于最新周期，则更新当前周期
    private void modernizeIdProducer(Ider ider, IdProducer idProducer) {
        Period modernPeriod = new Period(ider.getPeriodType(), new Date());

        Period period = new Period(ider.getPeriodType(), idProducer.getCurrentPeriod());
        if (period.compareTo(modernPeriod) < 0) {
            int modernStartId = calcModernStartId(ider, idProducer, modernPeriod);
            idProducer.setCurrentPeriod(modernPeriod.getDate());
            idProducer.setCurrentId((long) modernStartId);
            logger.info("被现代化后的id生产者：{}", idProducer);
        }
    }

    // 计算生产者跳跃到最新周期时的开始id
    private int calcModernStartId(Ider ider, IdProducer idProducer, Period modernPeriod) {
        int modernStartId = (int) (idProducer.getCurrentId() % ider.getFactor());
        if (ider.getMaxId() == null) {
            return modernStartId;
        }
        int growPerPeriod = ider.getFactor() - (int) (ider.getMaxId() % ider.getFactor());
        Period anchorPeriod = new Period(ider.getPeriodType(), idProducer.getCurrentPeriod());
        while (anchorPeriod.compareTo(modernPeriod) < 0) {
            anchorPeriod = anchorPeriod.grow(1);
            modernStartId = (modernStartId + growPerPeriod) % ider.getFactor();
        }
        return modernStartId;
    }
}
