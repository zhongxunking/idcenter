/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-21 20:56 创建
 */
package org.antframework.ids.facade.api.manage;

import org.antframework.ids.facade.order.AddOrModifyIderOrder;
import org.antframework.ids.facade.order.ModifyIderCurrentIdOrder;
import org.antframework.ids.facade.order.ModifyIderCurrentPeriodOrder;
import org.antframework.ids.facade.result.AddOrModifyIderResult;
import org.antframework.ids.facade.result.ModifyIderCurrentIdResult;
import org.antframework.ids.facade.result.ModifyIderCurrentPeriodResult;

/**
 * id提供者管理服务
 */
public interface IderManageService {

    /**
     * 新增或修改id提供者
     */
    AddOrModifyIderResult addOrModifyIder(AddOrModifyIderOrder order);

    /**
     * 修改id提供者当前周期
     */
    ModifyIderCurrentPeriodResult modifyIderCurrentPeriod(ModifyIderCurrentPeriodOrder order);

    /**
     * 修改id提供者当前id
     */
    ModifyIderCurrentIdResult modifyIderCurrentId(ModifyIderCurrentIdOrder order);
}
