/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-21 21:04 创建
 */
package org.antframework.idcenter.biz.provider;

import org.antframework.common.util.facade.EmptyResult;
import org.antframework.idcenter.facade.api.IderService;
import org.antframework.idcenter.facade.order.*;
import org.antframework.idcenter.facade.result.FindIderResult;
import org.antframework.idcenter.facade.result.QueryIdersResult;
import org.bekit.service.ServiceEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * id提供者服务提供者
 */
@Service
public class IderServiceProvider implements IderService {
    @Autowired
    private ServiceEngine serviceEngine;

    @Override
    public EmptyResult addIder(AddIderOrder order) {
        return serviceEngine.execute("addIderService", order);
    }

    @Override
    public EmptyResult modifyIderMax(ModifyIderMaxOrder order) {
        return serviceEngine.execute("modifyIderMaxService", order);
    }

    @Override
    public EmptyResult modifyIderFactor(ModifyIderFactorOrder order) {
        return serviceEngine.execute("modifyIderFactorService", order);
    }

    @Override
    public EmptyResult modifyIderCurrent(ModifyIderCurrentOrder order) {
        return serviceEngine.execute("modifyIderCurrentService", order);
    }

    @Override
    public EmptyResult deleteIder(DeleteIderOrder order) {
        return serviceEngine.execute("deleteIderService", order);
    }

    @Override
    public FindIderResult findIder(FindIderOrder order) {
        return serviceEngine.execute("findIderService", order);
    }

    @Override
    public QueryIdersResult queryIders(QueryIdersOrder order) {
        return serviceEngine.execute("queryIdersService", order);
    }
}
