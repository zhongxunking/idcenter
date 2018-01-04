/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-28 20:58 创建
 */
package org.antframework.idcenter.biz.service;

import org.antframework.common.util.facade.EmptyResult;
import org.antframework.idcenter.dal.dao.IderDao;
import org.antframework.idcenter.dal.dao.ProducerDao;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.dal.entity.Producer;
import org.antframework.idcenter.facade.order.DeleteIderOrder;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
    public void execute(ServiceContext<DeleteIderOrder, EmptyResult> context) {
        DeleteIderOrder order = context.getOrder();

        Ider ider = iderDao.findLockByIdCode(order.getIdCode());
        if (ider == null) {
            return;
        }

        List<Producer> producers = producerDao.findLockByIdCodeOrderByIndexAsc(ider.getIdCode());
        for (Producer producer : producers) {
            producerDao.delete(producer);
        }
        iderDao.delete(ider);
    }
}
