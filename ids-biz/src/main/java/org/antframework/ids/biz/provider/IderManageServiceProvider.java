/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-21 21:04 创建
 */
package org.antframework.ids.biz.provider;

import org.antframework.ids.facade.api.manage.IderManageService;
import org.antframework.ids.facade.order.*;
import org.antframework.ids.facade.result.*;
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
    public AddIderResult addIder(AddIderOrder order) {
        return serviceEngine.execute("addIderService", order);
    }

    @Override
    public ModifyIderMaxIdResult modifyIderMaxId(ModifyIderMaxIdOrder order) {
        return serviceEngine.execute("modifyIderMaxIdService", order);
    }

    @Override
    public ModifyIderFactorResult modifyIderFactor(ModifyIderFactorOrder order) {
        return serviceEngine.execute("modifyIderFactorService", order);
    }

    @Override
    public ModifyIderCurrentResult modifyIderCurrent(ModifyIderCurrentOrder order) {
        return serviceEngine.execute("modifyIderCurrentService", order);
    }

    @Override
    public DeleteIderResult deleteIder(DeleteIderOrder order) {
        return serviceEngine.execute("deleteIderService", order);
    }

    @Override
    public QueryIderResult queryIder(QueryIderOrder order) {
        return serviceEngine.execute("queryIderService", order);
    }
}
