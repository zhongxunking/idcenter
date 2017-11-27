/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-21 21:04 创建
 */
package org.antframework.ids.biz.provider;

import org.antframework.ids.facade.api.manage.IderManageService;
import org.antframework.ids.facade.order.AddOrModifyIderOrder;
import org.antframework.ids.facade.order.ModifyIderProducerNumberOrder;
import org.antframework.ids.facade.order.QueryIderOrder;
import org.antframework.ids.facade.result.AddOrModifyIderResult;
import org.antframework.ids.facade.result.ModifyIderProducerNumberResult;
import org.antframework.ids.facade.result.QueryIderResult;
import org.bekit.service.ServiceEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * id提供者管理服务提供者
 */
@Service
public class IderManageServiceProvider implements IderManageService {
    @Autowired
    private ServiceEngine serviceEngine;

    @Override
    public AddOrModifyIderResult addOrModifyIder(AddOrModifyIderOrder order) {
        return serviceEngine.execute("addOrModifyIderService", order);
    }

    @Override
    public ModifyIderProducerNumberResult modifyIderProducerNumber(ModifyIderProducerNumberOrder order) {
        return serviceEngine.execute("modifyIderProducerNumberService", order);
    }

    @Override
    public QueryIderResult queryIder(QueryIderOrder order) {
        return serviceEngine.execute("queryIderService", order);
    }
}
