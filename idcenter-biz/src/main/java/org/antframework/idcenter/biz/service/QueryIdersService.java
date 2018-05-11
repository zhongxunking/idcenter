/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-27 20:19 创建
 */
package org.antframework.idcenter.biz.service;

import org.antframework.boot.bekit.CommonQueries;
import org.antframework.idcenter.biz.service.converter.IderInfoConverter;
import org.antframework.idcenter.biz.util.QueryUtils;
import org.antframework.idcenter.dal.dao.IderDao;
import org.antframework.idcenter.facade.order.QueryIdersOrder;
import org.antframework.idcenter.facade.result.QueryIdersResult;
import org.bekit.service.ServiceEngine;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 查询id提供者服务
 */
@Service
public class QueryIdersService {
    @Autowired
    private ServiceEngine serviceEngine;
    @Autowired
    private IderInfoConverter infoConverter;

    @ServiceExecute
    public void execute(ServiceContext<QueryIdersOrder, QueryIdersResult> context) {
        QueryIdersOrder order = context.getOrder();
        QueryIdersResult result = context.getResult();

        CommonQueries.CommonQueryResult commonQueryResult = serviceEngine.execute(CommonQueries.SERVICE_NAME, order, QueryUtils.buildCommonQueryAttachment(IderDao.class));
        commonQueryResult.convertTo(result, infoConverter);
    }
}
