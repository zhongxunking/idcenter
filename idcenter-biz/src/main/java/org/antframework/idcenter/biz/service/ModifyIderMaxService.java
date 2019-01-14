/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-05 23:50 创建
 */
package org.antframework.idcenter.biz.service;

import org.antframework.common.util.facade.BizException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.EmptyResult;
import org.antframework.common.util.facade.Status;
import org.antframework.idcenter.dal.dao.IderDao;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.facade.order.ModifyIderMaxOrder;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * 修改id提供者的最大数据服务
 */
@Service(enableTx = true)
public class ModifyIderMaxService {
    private static final Logger logger = LoggerFactory.getLogger(ModifyIderMaxService.class);
    @Autowired
    private IderDao iderDao;

    @ServiceExecute
    public void execute(ServiceContext<ModifyIderMaxOrder, EmptyResult> context) {
        ModifyIderMaxOrder order = context.getOrder();

        Ider ider = iderDao.findLockByIderId(order.getIderId());
        if (ider == null) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", order.getIderId()));
        }
        checkNewMaxId(order, ider);

        logger.info("id提供者修改最大数据前：{}", ider);
        ider.setMaxId(order.getNewMaxId());
        ider.setMaxAmount(order.getNewMaxAmount());
        iderDao.save(ider);
        logger.info("id提供者修改最大数据后：{}", ider);
    }

    // 校验新的id最大值
    private void checkNewMaxId(ModifyIderMaxOrder order, Ider ider) {
        if (Objects.equals(order.getNewMaxId(), ider.getMaxId())) {
            return;
        }
        if (order.getNewMaxId() == null) {
            if (ider.getMaxId() % ider.getFactor() != 0) {
                throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), "将id最大值改为不限制的前提是：现有id最大值必须是因数的整数倍");
            }
        } else if (ider.getMaxId() == null) {
            if (order.getNewMaxId() % ider.getFactor() != 0) {
                throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), "将id最大值由不限制改为固定大小的前提是：改后的id最大值必须是因数的整数倍");
            }
        } else {
            if (Math.abs(order.getNewMaxId() - ider.getMaxId()) % ider.getFactor() != 0) {
                throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), "id最大值被修改的差值必须是因数的整数倍");
            }
            if (order.getNewMaxId() < ider.getFactor()) {
                throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("新的id最大值[%d]不能小于因数[%d]", order.getNewMaxId(), ider.getFactor()));
            }
        }
    }
}
