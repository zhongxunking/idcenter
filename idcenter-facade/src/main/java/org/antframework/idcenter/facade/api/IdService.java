/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-29 21:47 创建
 */
package org.antframework.idcenter.facade.api;

import org.antframework.idcenter.facade.order.AcquireIdsOrder;
import org.antframework.idcenter.facade.result.AcquireIdsResult;

/**
 * id服务
 */
public interface IdService {

    /**
     * 获取批量id
     */
    AcquireIdsResult acquireIds(AcquireIdsOrder order);
}
