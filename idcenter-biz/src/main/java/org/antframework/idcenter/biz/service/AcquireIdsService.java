/*
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-02 20:48 创建
 */
package org.antframework.idcenter.biz.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antframework.common.util.facade.BizException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.Status;
import org.antframework.common.util.id.Period;
import org.antframework.idcenter.biz.util.IdProducers;
import org.antframework.idcenter.dal.dao.IdProducerDao;
import org.antframework.idcenter.dal.dao.IderDao;
import org.antframework.idcenter.dal.entity.IdProducer;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.facade.order.AcquireIdsOrder;
import org.antframework.idcenter.facade.result.AcquireIdsResult;
import org.antframework.idcenter.facade.vo.IdSegment;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceBefore;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 获取批量id服务
 */
@Service(enableTx = true)
@AllArgsConstructor
@Slf4j
public class AcquireIdsService {
    // 随机数生成器
    private static final Random RANDOM = new Random();

    // id提供者dao
    private final IderDao iderDao;
    // id生产者dao
    private final IdProducerDao idProducerDao;

    @ServiceBefore
    public void before(ServiceContext<AcquireIdsOrder, AcquireIdsResult> context) {
        AcquireIdsOrder order = context.getOrder();

        Ider ider = iderDao.findByIderId(order.getIderId());
        if (ider == null) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", order.getIderId()));
        }
        context.getAttachment().put(Ider.class, ider);
    }

    @ServiceExecute
    public void execute(ServiceContext<AcquireIdsOrder, AcquireIdsResult> context) {
        AcquireIdsOrder order = context.getOrder();
        AcquireIdsResult result = context.getResult();
        Ider ider = (Ider) context.getAttachment().get(Ider.class);
        // 随机选择一个id生产者
        IdProducer idProducer = idProducerDao.findLockByIderIdAndIndex(ider.getIderId(), RANDOM.nextInt(ider.getFactor()));
        if (idProducer == null) {
            throw new BizException(Status.FAIL, CommonResultCode.ILLEGAL_STATE.getCode(), String.format("id提供者[%s]的因数被修改，请重试", ider.getIderId()));
        }
        // 刷新ider（id生产者被锁住前因数可能会被修改，在此更新到最新因数）
        ider = iderDao.findByIderId(ider.getIderId());
        log.info("被获取id的id提供者：{}", ider);
        log.info("生产id前的id生产者：{}", idProducer);
        // 现代化id生产者
        modernizeIdProducer(ider, idProducer);
        // 生产id
        List<IdSegment> idSegments = IdProducers.produce(ider, idProducer, order.getAmount());
        result.setIdSegments(idSegments);
        // 更新id生产者
        idProducerDao.save(idProducer);
        log.info("生产id后的id生产者：{}", idProducer);
    }

    // 如果id生产者的当前周期小于最新周期，则更新当前周期
    private void modernizeIdProducer(Ider ider, IdProducer idProducer) {
        Period modernPeriod = new Period(ider.getPeriodType(), new Date());

        Period period = new Period(ider.getPeriodType(), idProducer.getCurrentPeriod());
        if (period.compareTo(modernPeriod) < 0) {
            idProducer.setCurrentPeriod(modernPeriod.getDate());
            idProducer.setCurrentId((long) idProducer.getIndex());
            log.info("被现代化后的id生产者：{}", idProducer);
        }
    }
}
