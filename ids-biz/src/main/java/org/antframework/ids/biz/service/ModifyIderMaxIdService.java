/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-02 13:53 创建
 */
package org.antframework.ids.biz.service;

import org.antframework.boot.bekit.AntBekitException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.Status;
import org.antframework.ids.dal.dao.IderDao;
import org.antframework.ids.dal.entity.Ider;
import org.antframework.ids.facade.order.ModifyIderMaxIdOrder;
import org.antframework.ids.facade.result.ModifyIderMaxIdResult;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 修改id提供者的最大id服务
 */
@Service(enableTx = true)
public class ModifyIderMaxIdService {
    @Autowired
    private IderDao iderDao;

    @ServiceExecute
    public void execute(ServiceContext<ModifyIderMaxIdOrder, ModifyIderMaxIdResult> context) {
        ModifyIderMaxIdOrder order = context.getOrder();

        Ider ider = iderDao.findLockByIdCode(order.getIdCode());
        if (ider == null) {
            throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", order.getIdCode()));
        }
        checkNewMaxId(order, ider);
        ider.setMaxId(order.getNewMaxId());
        iderDao.save(ider);
    }

    // 校验新的id最大值
    private void checkNewMaxId(ModifyIderMaxIdOrder order, Ider ider) {
        if (order.getNewMaxId() == ider.getMaxId()) {
            return;
        }
        if (order.getNewMaxId() == null) {
            if (ider.getMaxId() % ider.getFactor() != 0) {
                throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), "将id最大值改为不限制的前提是：现有id最大值必须是因数的整数倍");
            }
        } else if (ider.getMaxId() == null) {
            if (order.getNewMaxId() % ider.getFactor() != 0) {
                throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), "将id最大值由不限制改为固定大小的前提是：改后的id最大值必须是因数的整数倍");
            }
        } else {
            if (Math.abs(order.getNewMaxId() - ider.getMaxId()) % ider.getFactor() != 0) {
                throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), "id最大值被修改的差值必须是因数的整数倍");
            }
            if (order.getNewMaxId() < ider.getFactor()) {
                throw new AntBekitException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id最大值不能小于因数[%d]", ider.getFactor()));
            }
        }
    }
}
