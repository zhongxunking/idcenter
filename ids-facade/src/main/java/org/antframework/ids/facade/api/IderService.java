/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-29 21:47 创建
 */
package org.antframework.ids.facade.api;

import org.antframework.ids.facade.order.AcquireIdsOrder;
import org.antframework.ids.facade.result.AcquireIdsResult;

/**
 * id提供者服务
 */
public interface IderService {

    /**
     * 获取批量id
     */
    AcquireIdsResult acquireIds(AcquireIdsOrder order);
}
