/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-28 20:58 创建
 */
package org.antframework.ids.biz.service;

import org.antframework.ids.dal.dao.IderDao;
import org.antframework.ids.dal.dao.ProducerDao;
import org.antframework.ids.dal.entity.Ider;
import org.antframework.ids.facade.order.DeleteIderOrder;
import org.antframework.ids.facade.result.DeleteIderResult;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 删除id提供者服务
 */
@Service(enableTx = true)
public class DeleteIderService {
    @Autowired
    private IderDao iderDao;
    @Autowired
    private ProducerDao producerDao;

    @ServiceExecute
    public void execute(ServiceContext<DeleteIderOrder, DeleteIderResult> context) {
        DeleteIderOrder order = context.getOrder();

        Ider ider = iderDao.findLockByIdCode(order.getIdCode());
        if (ider == null) {
            return;
        }
        producerDao.deleteByIdCode(ider.getIdCode());
        iderDao.delete(ider);
    }
}
