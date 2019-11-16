/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-05 23:50 创建
 */
package org.antframework.idcenter.biz.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antframework.common.util.facade.BizException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.EmptyResult;
import org.antframework.common.util.facade.Status;
import org.antframework.idcenter.biz.util.IdProducers;
import org.antframework.idcenter.biz.util.Iders;
import org.antframework.idcenter.dal.dao.IdProducerDao;
import org.antframework.idcenter.dal.dao.IderDao;
import org.antframework.idcenter.dal.entity.IdProducer;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.facade.order.ModifyIderMaxOrder;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;

/**
 * 修改id提供者的最大数据服务
 */
@Service(enableTx = true)
@AllArgsConstructor
@Slf4j
public class ModifyIderMaxService {
    // id提供者dao
    private final IderDao iderDao;
    // id生产者dao
    private final IdProducerDao idProducerDao;

    @ServiceExecute
    public void execute(ServiceContext<ModifyIderMaxOrder, EmptyResult> context) {
        ModifyIderMaxOrder order = context.getOrder();
        // 校验入参
        Ider ider = iderDao.findLockByIderId(order.getIderId());
        if (ider == null) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", order.getIderId()));
        }
        if (order.getNewMaxId() != null && order.getNewMaxId() < ider.getFactor()) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("新的id最大值[%d]不能小于因数[%d]", order.getNewMaxId(), ider.getFactor()));
        }
        // 修改id提供者的最大数据
        log.info("id提供者被修改最大数据前：{}", ider);
        ider.setMaxId(order.getNewMaxId());
        ider.setMaxAmount(order.getNewMaxAmount());
        iderDao.save(ider);
        log.info("id提供者被修改最大数据后：{}", ider);
        // 计算进度最靠前的id生产者
        IdProducer maxIdProducer = idProducerDao.findLockByIderIdOrderByIndexAsc(ider.getIderId())
                .stream()
                .max(IdProducers::compare)
                .get();
        // 更新所有id生产者的当前数据
        Iders.modifyIderCurrent(ider.getIderId(), maxIdProducer.getCurrentPeriod(), maxIdProducer.getCurrentId());
    }
}
