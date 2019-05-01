/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-17 23:21 创建
 */
package org.antframework.idcenter.biz.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antframework.common.util.facade.BizException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.EmptyResult;
import org.antframework.common.util.facade.Status;
import org.antframework.idcenter.dal.dao.IderDao;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.facade.order.ModifyIderNameOrder;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;

/**
 * 修改id提供者的名称服务
 */
@Service(enableTx = true)
@AllArgsConstructor
@Slf4j
public class ModifyIderNameService {
    // id提供者dao
    private final IderDao iderDao;

    @ServiceExecute
    public void execute(ServiceContext<ModifyIderNameOrder, EmptyResult> context) {
        ModifyIderNameOrder order = context.getOrder();

        Ider ider = iderDao.findLockByIderId(order.getIderId());
        if (ider == null) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", order.getIderId()));
        }
        log.info("id提供者被修改名称前：{}", ider);
        ider.setIderName(order.getNewIderName());
        iderDao.save(ider);
        log.info("id提供者被修改名称后：{}", ider);
    }
}
