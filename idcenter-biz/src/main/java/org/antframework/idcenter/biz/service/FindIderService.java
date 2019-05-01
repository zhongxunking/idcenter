/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-02-01 21:40 创建
 */
package org.antframework.idcenter.biz.service;

import lombok.AllArgsConstructor;
import org.antframework.idcenter.biz.service.converter.IderInfoConverter;
import org.antframework.idcenter.dal.dao.IderDao;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.facade.order.FindIderOrder;
import org.antframework.idcenter.facade.result.FindIderResult;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;

/**
 * 查找id提供者服务
 */
@Service
@AllArgsConstructor
public class FindIderService {
    // id提供者dao
    private final IderDao iderDao;
    // id提供者info转换器
    private final IderInfoConverter infoConverter;

    @ServiceExecute
    public void execute(ServiceContext<FindIderOrder, FindIderResult> context) {
        FindIderOrder order = context.getOrder();
        FindIderResult result = context.getResult();

        Ider ider = iderDao.findByIderId(order.getIderId());
        if (ider != null) {
            result.setIder(infoConverter.convert(ider));
        }
    }
}
