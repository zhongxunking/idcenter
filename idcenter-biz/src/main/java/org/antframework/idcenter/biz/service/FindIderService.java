/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-02-01 21:40 创建
 */
package org.antframework.idcenter.biz.service;

import org.antframework.idcenter.biz.service.converter.IderInfoConverter;
import org.antframework.idcenter.dal.dao.IderDao;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.facade.order.FindIderOrder;
import org.antframework.idcenter.facade.result.FindIderResult;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 查找id提供者服务
 */
@Service
public class FindIderService {
    @Autowired
    private IderDao iderDao;
    @Autowired
    private IderInfoConverter infoConverter;

    @ServiceExecute
    public void execute(ServiceContext<FindIderOrder, FindIderResult> context) {
        FindIderOrder order = context.getOrder();
        FindIderResult result = context.getResult();

        Ider ider = iderDao.findByIdCode(order.getIdCode());
        if (ider != null) {
            result.setIderInfo(infoConverter.convert(ider));
        }
    }
}
