/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-27 20:19 创建
 */
package org.antframework.idcenter.biz.service;

import org.antframework.boot.bekit.CommonQueryConstant;
import org.antframework.boot.bekit.CommonQueryResult;
import org.antframework.idcenter.biz.service.converter.IderInfoConverter;
import org.antframework.idcenter.biz.util.QueryUtils;
import org.antframework.idcenter.dal.dao.IderDao;
import org.antframework.idcenter.facade.order.QueryIderOrder;
import org.antframework.idcenter.facade.result.QueryIderResult;
import org.bekit.service.ServiceEngine;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 查询id提供者服务
 */
@Service
public class QueryIderService {
    @Autowired
    private ServiceEngine serviceEngine;
    @Autowired
    private IderInfoConverter infoConverter;

    @ServiceExecute
    public void execute(ServiceContext<QueryIderOrder, QueryIderResult> context) {
        QueryIderOrder order = context.getOrder();
        QueryIderResult result = context.getResult();

        CommonQueryResult commonQueryResult = serviceEngine.execute(CommonQueryConstant.SERVICE_NAME, order, QueryUtils.buildCommonQueryAttachment(IderDao.class));
        commonQueryResult.convertTo(result, infoConverter);
    }
}
