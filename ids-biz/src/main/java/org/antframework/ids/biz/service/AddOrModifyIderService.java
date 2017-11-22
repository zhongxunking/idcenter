/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-21 21:06 创建
 */
package org.antframework.ids.biz.service;

import org.antframework.ids.dal.dao.IderDao;
import org.antframework.ids.dal.entity.Ider;
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

    @ServiceExecute
    public void execute(ServiceContext<AddOrModifyIderOrder, AddOrModifyIderResult> context) {
        AddOrModifyIderOrder order = context.getOrder();

        Ider ider = iderDao.findLockByIdCode(order.getIdCode());
        if (ider == null) {
            ider = buildIder(order);
        } else {
            BeanUtils.copyProperties(order, ider);
        }
        iderDao.save(ider);
    }

    // 构建id提供者
    private Ider buildIder(AddOrModifyIderOrder addIderOrder) {
        Ider ider = new Ider();
        BeanUtils.copyProperties(addIderOrder, ider);
        ider.setCurrentPeriod(PeriodUtils.parsePeriod(addIderOrder.getPeriodType(), new Date()));
        ider.setCurrentId(0L);

        return ider;
    }
}
