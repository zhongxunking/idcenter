/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-21 21:06 创建
 */
package org.antframework.ids.biz.service;

import org.antframework.boot.bekit.AntBekitException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.Status;
import org.antframework.ids.dal.dao.IderDao;
import org.antframework.ids.dal.dao.ProducerDao;
import org.antframework.ids.dal.entity.Ider;
import org.antframework.ids.dal.entity.Producer;
import org.antframework.ids.facade.order.AddOrModifyIderOrder;
import org.antframework.ids.facade.result.AddOrModifyIderResult;
import org.antframework.ids.facade.util.PeriodUtils;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * 新增或修改id提供者服务
 */
@Service(enableTx = true)
public class AddOrModifyIderService {
    @Autowired
    private IderDao iderDao;
    @Autowired
    private ProducerDao producerDao;

    @ServiceExecute
    public void execute(ServiceContext<AddOrModifyIderOrder, AddOrModifyIderResult> context) {
        AddOrModifyIderOrder order = context.getOrder();

        Ider ider = iderDao.findLockByIdCode(order.getIdCode());
        if (ider == null) {
            ider = buildIder(order);
            producerDao.save(buildProducer(ider));
        } else {
            if (order.getPeriodType() != ider.getPeriodType()) {
                throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), "周期类型不能修改");
            }
            checkMaxId(order, ider);
            BeanUtils.copyProperties(order, ider);
        }
        if (ider.getMaxId() < ider.getFactor()) {
            throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id最大值不能小于因数[%d]", ider.getFactor()));
        }

        iderDao.save(ider);
    }

    // 构建id提供者
    private Ider buildIder(AddOrModifyIderOrder addOrModifyIderOrder) {
        Ider ider = new Ider();
        BeanUtils.copyProperties(addOrModifyIderOrder, ider);
        ider.setFactor(1);

        return ider;
    }

    // 构建生产者
    private Producer buildProducer(Ider ider) {
        Producer producer = new Producer();
        producer.setIdCode(ider.getIdCode());
        producer.setIndex(0);
        producer.setCurrentPeriod(PeriodUtils.parse(ider.getPeriodType(), new Date()));
        producer.setCurrentId(0L);

        return producer;
    }

    private void checkMaxId(AddOrModifyIderOrder order, Ider ider) {
        if (order.getMaxId() == ider.getMaxId()) {
            return;
        }
        if (order.getMaxId() == null) {
            if (ider.getMaxId() % ider.getFactor() != 0) {
                throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), "将id最大值改为不限制的前提是：现有id最大值必须是因数的整数倍");
            }
        } else if (ider.getMaxId() == null) {
            if (order.getMaxId() % ider.getFactor() != 0) {
                throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), "将id最大值由不限制改为固定大小的前提是：改后的id最大值必须是因数的整数倍");
            }
        } else {
            if (Math.abs(order.getMaxId() - ider.getMaxId()) % ider.getFactor() != 0) {
                throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), "id最大值被修改的差值必须是因数的整数倍");
            }
        }
    }
}
