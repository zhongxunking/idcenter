/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-17 23:21 创建
 */
package org.antframework.idcenter.biz.service;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 修改id提供者的名称服务
 */
@Service(enableTx = true)
public class ModifyIderNameService {
    private static final Logger logger = LoggerFactory.getLogger(ModifyIderNameService.class);
    @Autowired
    private IderDao iderDao;

    @ServiceExecute
    public void execute(ServiceContext<ModifyIderNameOrder, EmptyResult> context) {
        ModifyIderNameOrder order = context.getOrder();

        Ider ider = iderDao.findLockByIderId(order.getIderId());
        if (ider == null) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", order.getIderId()));
        }
        logger.info("id提供者被修改名称前：{}", ider);
        ider.setIderName(order.getNewIderName());
        iderDao.save(ider);
        logger.info("id提供者被修改名称后：{}", ider);
    }
}
