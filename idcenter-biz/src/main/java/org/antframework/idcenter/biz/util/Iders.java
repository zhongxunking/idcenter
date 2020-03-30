/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-20 19:15 创建
 */
package org.antframework.idcenter.biz.util;

import org.antframework.boot.core.Contexts;
import org.antframework.common.util.facade.EmptyResult;
import org.antframework.common.util.facade.FacadeUtils;
import org.antframework.idcenter.facade.api.IderService;
import org.antframework.idcenter.facade.info.IderInfo;
import org.antframework.idcenter.facade.order.AcquireIdsOrder;
import org.antframework.idcenter.facade.order.FindIderOrder;
import org.antframework.idcenter.facade.order.ModifyIderCurrentOrder;
import org.antframework.idcenter.facade.result.AcquireIdsResult;
import org.antframework.idcenter.facade.result.FindIderResult;
import org.antframework.idcenter.facade.vo.IdSegment;

import java.util.Date;
import java.util.List;

/**
 * id提供者操作类
 */
public final class Iders {
    // id提供者服务
    private static final IderService IDER_SERVICE = Contexts.getApplicationContext().getBean(IderService.class);

    /**
     * 修改id提供者当前数据
     *
     * @param iderId           id提供者的id（id编码）
     * @param newCurrentPeriod 新的当前周期（null表示无周期）
     * @param newCurrentId     新的当前Id（未使用）
     */
    public static void modifyIderCurrent(String iderId, Date newCurrentPeriod, Long newCurrentId) {
        ModifyIderCurrentOrder order = new ModifyIderCurrentOrder();
        order.setIderId(iderId);
        order.setNewCurrentPeriod(newCurrentPeriod);
        order.setNewCurrentId(newCurrentId);

        EmptyResult result = IDER_SERVICE.modifyIderCurrent(order);
        FacadeUtils.assertSuccess(result);
    }

    /**
     * 获取批量id
     *
     * @param iderId id提供者的id（id编码）
     * @param amount 数量
     * @return 批量id
     */
    public static List<IdSegment> acquireIds(String iderId, int amount) {
        AcquireIdsOrder order = new AcquireIdsOrder();
        order.setIderId(iderId);
        order.setAmount(amount);

        AcquireIdsResult result = IDER_SERVICE.acquireIds(order);
        FacadeUtils.assertSuccess(result);
        return result.getIdSegments();
    }

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
