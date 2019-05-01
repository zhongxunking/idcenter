/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-25 20:03 创建
 */
package org.antframework.idcenter.biz.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antframework.common.util.facade.BizException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.EmptyResult;
import org.antframework.common.util.facade.Status;
import org.antframework.idcenter.biz.util.IdProducers;
import org.antframework.idcenter.dal.dao.IdProducerDao;
import org.antframework.idcenter.dal.dao.IderDao;
import org.antframework.idcenter.dal.entity.IdProducer;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.facade.order.ModifyIderFactorOrder;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 修改id提供者的因数服务
 */
@Service(enableTx = true)
@AllArgsConstructor
@Slf4j
public class ModifyIderFactorService {
    // id提供者dao
    private final IderDao iderDao;
    // id生产者dao
    private final IdProducerDao idProducerDao;

    @ServiceExecute
    public void execute(ServiceContext<ModifyIderFactorOrder, EmptyResult> context) {
        ModifyIderFactorOrder order = context.getOrder();

        Ider ider = iderDao.findLockByIderId(order.getIderId());
        if (ider == null) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", order.getIderId()));
        }
        if (ider.getMaxId() != null && order.getNewFactor() > ider.getMaxId()) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("新的因数[%d]不能大于id提供者[%s]的最大id[%d]", order.getNewFactor(), ider.getIderId(), ider.getMaxId()));
        }
        if (Objects.equals(order.getNewFactor(), ider.getFactor())) {
            return;
        }
        log.info("id提供者被修改因数前：{}", ider);
        List<IdProducer> idProducers = idProducerDao.findLockByIderIdOrderByIndexAsc(ider.getIderId());
        // 计算进度最靠前的id生产者
        IdProducer maxIdProducer = null;
        for (IdProducer idProducer : idProducers) {
            log.info("现有的id生产者：{}", idProducer);
            if (maxIdProducer == null || IdProducers.compare(idProducer, maxIdProducer) > 0) {
                maxIdProducer = idProducer;
            }
        }
        Date maxCurrentPeriod = maxIdProducer.getCurrentPeriod();
        Long maxCurrentId = maxIdProducer.getCurrentId();
        // 设置新的id生产者
        for (int i = 0; i < order.getNewFactor(); i++) {
            IdProducer idProducer = i < idProducers.size() ? idProducers.get(i) : new IdProducer();
            resetIdProducer(idProducer, ider, i, maxCurrentPeriod, maxCurrentId);
            idProducerDao.save(idProducer);
            log.info("新的id提供者：{}", idProducer);
        }
        // 删除多余的id生产者
        for (int i = order.getNewFactor(); i < idProducers.size(); i++) {
            IdProducer idProducer = idProducers.get(i);
            idProducerDao.delete(idProducer);
            log.info("删除id提供者：{}", idProducer);
        }
        // 修改因数
        ider.setFactor(order.getNewFactor());
        iderDao.save(ider);
        log.info("id提供者被修改因数后：{}", ider);
    }

    // 重置id生产者
    private void resetIdProducer(IdProducer idProducer, Ider ider, int index, Date maxCurrentPeriod, Long maxCurrentId) {
        idProducer.setIderId(ider.getIderId());
        idProducer.setIndex(index);
        idProducer.setCurrentPeriod(maxCurrentPeriod);
        idProducer.setCurrentId(maxCurrentId);
        IdProducers.grow(ider, idProducer, index);
    }
}
