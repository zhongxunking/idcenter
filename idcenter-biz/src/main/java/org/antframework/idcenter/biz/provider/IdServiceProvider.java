/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-02 20:46 创建
 */
package org.antframework.idcenter.biz.provider;

import org.antframework.idcenter.facade.api.IdService;
import org.antframework.idcenter.facade.order.AcquireIdsOrder;
import org.antframework.idcenter.facade.result.AcquireIdsResult;
import org.bekit.service.ServiceEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * id服务提供者
 */
@Service
public class IdServiceProvider implements IdService {
    @Autowired
    private ServiceEngine serviceEngine;

    @Override
    public AcquireIdsResult acquireIds(AcquireIdsOrder order) {
        return serviceEngine.execute("acquireIdsService", order);
    }
}
