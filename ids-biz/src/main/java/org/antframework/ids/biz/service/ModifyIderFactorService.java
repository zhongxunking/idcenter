/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-25 20:03 创建
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
import org.antframework.ids.facade.order.ModifyIderFactorOrder;
import org.antframework.ids.facade.result.ModifyIderFactorResult;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 修改id提供者的因数服务
 */
@Service(enableTx = true)
public class ModifyIderFactorService {
    @Autowired
    private IderDao iderDao;
    @Autowired
    private ProducerDao producerDao;

    @ServiceExecute
    public void execute(ServiceContext<ModifyIderFactorOrder, ModifyIderFactorResult> context) {
        ModifyIderFactorOrder order = context.getOrder();

        Ider ider = iderDao.findLockByIdCode(order.getIdCode());
        if (ider == null) {
            throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", order.getIdCode()));
        }
        if (order.getNewFactor() > ider.getMaxId()) {
            throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("新的因数不能大于id提供者[%s]的最大id[%d]", ider.getIdCode(), ider.getMaxId()));
        }
        if (Math.max(order.getNewFactor(), ider.getFactor()) % Math.min(order.getNewFactor(), ider.getFactor()) != 0) {
            throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("因数要么成倍增加要么成倍减少，id提供者[%s]当前因数[%d]，期望因数[%d]不符合要求", ider.getIdCode(), ider.getFactor(), order.getNewFactor()));
        }

        if (order.getNewFactor() > ider.getFactor()) {
            addProducers(ider, order.getNewFactor());
        } else if (order.getNewFactor() < ider.getFactor()) {
            deleteProducers(ider, order.getNewFactor());
        }

        ider.setFactor(order.getNewFactor());
        iderDao.save(ider);
    }

    // 添加id生产者
    private void addProducers(Ider ider, int newFactor) {
        List<Producer> producers = producerDao.findLockByIdCodeOrderByIndexAsc(ider.getIdCode());
        for (int i = ider.getFactor(); i < newFactor; i++) {
            Producer producer = buildProducer(producers.get(i % ider.getFactor()), i, ider);
            producerDao.save(producer);
        }
    }

    // 构建id生产者
    private Producer buildProducer(Producer sourceProducer, int index, Ider ider) {
        Producer producer = new Producer();
        producer.setIdCode(sourceProducer.getIdCode());
        producer.setIndex(index);
        producer.setCurrentPeriod(sourceProducer.getCurrentPeriod());
        producer.setCurrentId(sourceProducer.getCurrentId());
        ProducerUtils.produce(producer, ider, index / ider.getFactor());

        return producer;
    }

    // 删除id生产者
    private void deleteProducers(Ider ider, int newFactor) {
        List<Producer> producers = producerDao.findLockByIdCodeOrderByIndexAsc(ider.getIdCode());
        for (int i = newFactor; i < ider.getFactor(); i++) {
            Producer deletingProducer = producers.get(i);
            updateProducer(producers.get(i % newFactor), deletingProducer);
            producerDao.delete(deletingProducer);
        }
    }

    // 更新id生产者
    private void updateProducer(Producer producer, Producer deletingProducer) {
        if (ProducerUtils.ProducerComparator.INSTANCE.compare(deletingProducer, producer) > 0) {
            producer.setCurrentPeriod(deletingProducer.getCurrentPeriod());
            producer.setCurrentId(deletingProducer.getCurrentId());
            producerDao.save(producer);
        }
    }
}
