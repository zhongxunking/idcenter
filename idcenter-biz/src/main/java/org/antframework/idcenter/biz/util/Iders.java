/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-20 19:15 创建
 */
package org.antframework.idcenter.biz.util;

import org.antframework.boot.core.Contexts;
import org.antframework.common.util.facade.FacadeUtils;
import org.antframework.idcenter.facade.api.IderService;
import org.antframework.idcenter.facade.info.IderInfo;
import org.antframework.idcenter.facade.order.FindIderOrder;
import org.antframework.idcenter.facade.result.FindIderResult;

/**
 * id提供者操作类
 */
public final class Iders {
    // id提供者服务
    private static final IderService IDER_SERVICE = Contexts.getApplicationContext().getBean(IderService.class);

    /**
     * 获取id提供者
     *
     * @param iderId id提供者的id（id编码）
     * @return id提供者（null表示无该id提供者）
     */
    public static IderInfo findIder(String iderId) {
        FindIderOrder order = new FindIderOrder();
        order.setIderId(iderId);

        FindIderResult result = IDER_SERVICE.findIder(order);
        FacadeUtils.assertSuccess(result);
        return result.getIder();
    }
}
