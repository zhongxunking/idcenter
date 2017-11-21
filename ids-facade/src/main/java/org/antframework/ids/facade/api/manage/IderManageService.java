/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-21 20:56 创建
 */
package org.antframework.ids.facade.api.manage;

import org.antframework.ids.facade.order.AddIderOrder;
import org.antframework.ids.facade.result.AddIderResult;

/**
 * id提供者管理服务
 */
public interface IderManageService {

    /**
     * 添加id提供者
     */
    AddIderResult addIder(AddIderOrder order);

}
