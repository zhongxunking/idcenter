/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-31 23:39 创建
 */
package org.antframework.idcenter.web.controller.manage;

import org.antframework.common.util.facade.*;
import org.antframework.idcenter.facade.api.manage.IderManageService;
import org.antframework.idcenter.facade.enums.PeriodType;
import org.antframework.idcenter.facade.order.*;
import org.antframework.idcenter.facade.result.QueryIderResult;
import org.antframework.idcenter.facade.vo.IderInfo;
import org.antframework.manager.facade.api.RelationService;
import org.antframework.manager.facade.enums.ManagerType;
import org.antframework.manager.facade.info.ManagerInfo;
import org.antframework.manager.facade.order.QueryManagerRelationOrder;
import org.antframework.manager.facade.result.QueryManagerRelationResult;
import org.antframework.manager.web.common.ManagerSessionAccessor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 *
 */
@RestController
@RequestMapping("/manage/ider")
public class IderManageController {
    @Autowired
    private IderManageService iderManageService;
    @Autowired
    private RelationService relationService;

    /**
     * 新增id提供者
     *
     * @param idCode     id编码（必填）
     * @param periodType 周期类型（必填）
     * @param maxId      id最大值（选填）
     * @param maxAmount  单次获取id最多数量（选填）
     * @return 新增结果
     */
    @RequestMapping("/add")
    public EmptyResult add(String idCode, PeriodType periodType, Long maxId, Integer maxAmount) {
        AddIderOrder order = new AddIderOrder();
        order.setIdCode(idCode);
        order.setPeriodType(periodType);
        order.setMaxId(maxId);
        order.setMaxAmount(maxAmount);

        return iderManageService.addIder(order);
    }

    /**
     * 修改id提供者的最大数据
     *
     * @param idCode       id编码（必填）
     * @param newMaxId     新的id最大值（选填）
     * @param newMaxAmount 新的单次获取id数量（选填）
     * @return 修改结果
     */
    @RequestMapping("/modifyMax")
    public EmptyResult modifyMax(String idCode, Long newMaxId, Integer newMaxAmount) {
        ModifyIderMaxOrder order = new ModifyIderMaxOrder();
        order.setIdCode(idCode);
        order.setNewMaxId(newMaxId);
        order.setNewMaxAmount(newMaxAmount);

        return iderManageService.modifyIderMax(order);
    }

    /**
     * 修改因数
     *
     * @param idCode    id编码（必填）
     * @param newFactor 新的因数（选填）
     * @return 修改结果
     */
    @RequestMapping("/modifyFactor")
    public EmptyResult modifyFactor(String idCode, int newFactor) {
        ModifyIderFactorOrder order = new ModifyIderFactorOrder();
        order.setIdCode(idCode);
        order.setNewFactor(newFactor);

        return iderManageService.modifyIderFactor(order);
    }

    /**
     * 修改当前数据
     *
     * @param idCode           id编码（必填）
     * @param newCurrentPeriod 新的当前周期（选填）
     * @param newCurrentId     新的当前id（必填）
     * @return 修改结果
     */
    @RequestMapping("/modifyCurrent")
    public EmptyResult modifyCurrent(String idCode, Date newCurrentPeriod, long newCurrentId) {
        ModifyIderCurrentOrder order = new ModifyIderCurrentOrder();
        order.setIdCode(idCode);
        order.setNewCurrentPeriod(newCurrentPeriod);
        order.setNewCurrentId(newCurrentId);

        return iderManageService.modifyIderCurrent(order);
    }

    /**
     * 删除
     *
     * @param idCode id编码（必填）
     * @return 删除结果
     */
    @RequestMapping("/delete")
    public EmptyResult delete(String idCode) {
        DeleteIderOrder order = new DeleteIderOrder();
        order.setIdCode(idCode);

        return iderManageService.deleteIder(order);
    }

    @RequestMapping("/queryManagedIder")
    public QueryManagedIderResult queryManagedIder(int pageNo, int pageSize, String idCode) {
        ManagerInfo manager = ManagerSessionAccessor.getManager();
        if (manager == null) {
            throw new BizException(Status.FAIL, CommonResultCode.ILLEGAL_STATE.getCode(), "未登陆或登陆已过期");
        }

        QueryManagedIderResult result = new QueryManagedIderResult();
        if (manager.getType() == ManagerType.ADMIN) {
            QueryIderOrder order = new QueryIderOrder();
            order.setPageNo(pageNo);
            order.setPageSize(pageSize);
            order.setIdCode(idCode);
            order.setPeriodType(null);

            QueryIderResult queryIderResult = iderManageService.queryIder(order);
            BeanUtils.copyProperties(queryIderResult, result);
        } else {
            QueryManagerRelationOrder order = new QueryManagerRelationOrder();
            order.setPageNo(pageNo);
            order.setPageSize(pageSize);
            order.setManagerId(manager.getManagerId());
            order.setTargetId(idCode);

            QueryManagerRelationResult queryManagerRelationResult = relationService.queryManagerRelation(order);
            if (!queryManagerRelationResult.isSuccess()) {
                throw new BizException(Status.FAIL, queryManagerRelationResult.getCode(), queryManagerRelationResult.getMessage());
            }
        }
        return result;
    }

    public static class QueryManagedIderResult extends AbstractQueryResult<IderInfo> {
    }
}
