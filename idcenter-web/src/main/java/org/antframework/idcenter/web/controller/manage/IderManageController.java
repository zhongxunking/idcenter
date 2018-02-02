/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-31 23:39 创建
 */
package org.antframework.idcenter.web.controller.manage;

import org.antframework.common.util.facade.AbstractQueryResult;
import org.antframework.common.util.facade.BizException;
import org.antframework.common.util.facade.EmptyResult;
import org.antframework.common.util.facade.Status;
import org.antframework.idcenter.facade.api.manage.IderManageService;
import org.antframework.idcenter.facade.enums.PeriodType;
import org.antframework.idcenter.facade.order.*;
import org.antframework.idcenter.facade.result.FindIderResult;
import org.antframework.idcenter.facade.result.QueryIderResult;
import org.antframework.idcenter.facade.vo.IderInfo;
import org.antframework.manager.facade.api.RelationService;
import org.antframework.manager.facade.enums.ManagerType;
import org.antframework.manager.facade.info.ManagerInfo;
import org.antframework.manager.facade.info.RelationInfo;
import org.antframework.manager.facade.order.DeleteRelationOrder;
import org.antframework.manager.facade.order.QueryManagerRelationOrder;
import org.antframework.manager.facade.result.QueryManagerRelationResult;
import org.antframework.manager.web.common.ManagerAssert;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * id提供者管理controller
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
     * @param maxId      id最大值（null表示不限制）
     * @param maxAmount  单次获取id最多数量（null表示不限制）
     * @return 新增结果
     */
    @RequestMapping("/add")
    public EmptyResult add(String idCode, PeriodType periodType, Long maxId, Integer maxAmount) {
        ManagerAssert.admin();
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
     * @param newMaxId     新的id最大值（null表示不限制）
     * @param newMaxAmount 新的单次获取id数量（null表示不限制）
     * @return 修改结果
     */
    @RequestMapping("/modifyMax")
    public EmptyResult modifyMax(String idCode, Long newMaxId, Integer newMaxAmount) {
        ManagerAssert.adminOrHaveRelation(idCode);
        ModifyIderMaxOrder order = new ModifyIderMaxOrder();
        order.setIdCode(idCode);
        order.setNewMaxId(newMaxId);
        order.setNewMaxAmount(newMaxAmount);

        return iderManageService.modifyIderMax(order);
    }

    /**
     * 修改id提供者的因数
     *
     * @param idCode    id编码（必填）
     * @param newFactor 新的因数（必填）
     * @return 修改结果
     */
    @RequestMapping("/modifyFactor")
    public EmptyResult modifyFactor(String idCode, int newFactor) {
        ManagerAssert.adminOrHaveRelation(idCode);
        ModifyIderFactorOrder order = new ModifyIderFactorOrder();
        order.setIdCode(idCode);
        order.setNewFactor(newFactor);

        return iderManageService.modifyIderFactor(order);
    }

    /**
     * 修改id提供者当前数据
     *
     * @param idCode           id编码（必填）
     * @param newCurrentPeriod 新的当前周期（null表示无周期）
     * @param newCurrentId     新的当前id（必填）
     * @return 修改结果
     */
    @RequestMapping("/modifyCurrent")
    public EmptyResult modifyCurrent(String idCode, Date newCurrentPeriod, long newCurrentId) {
        ManagerAssert.adminOrHaveRelation(idCode);
        ModifyIderCurrentOrder order = new ModifyIderCurrentOrder();
        order.setIdCode(idCode);
        order.setNewCurrentPeriod(newCurrentPeriod);
        order.setNewCurrentId(newCurrentId);

        return iderManageService.modifyIderCurrent(order);
    }

    /**
     * 删除id提供者
     *
     * @param idCode id编码（必填）
     * @return 删除结果
     */
    @RequestMapping("/delete")
    public EmptyResult delete(String idCode) {
        ManagerAssert.admin();
        // 删除id提供者与管理员的关系
        DeleteRelationOrder deleteRelationOrder = new DeleteRelationOrder();
        deleteRelationOrder.setTargetId(idCode);
        EmptyResult result = relationService.deleteRelation(deleteRelationOrder);
        if (!result.isSuccess()) {
            return result;
        }
        // 删除id提供者
        DeleteIderOrder order = new DeleteIderOrder();
        order.setIdCode(idCode);
        return iderManageService.deleteIder(order);
    }

    /**
     * 查询被管理的id提供者
     *
     * @param pageNo   页码（必填）
     * @param pageSize 每页大小（必填）
     * @param idCode   id编码（选填）
     * @return 查询结果
     */
    @RequestMapping("/queryManagedIder")
    public QueryManagedIderResult queryManagedIder(int pageNo, int pageSize, String idCode) {
        ManagerInfo manager = ManagerAssert.currentManager();
        if (manager.getType() == ManagerType.ADMIN) {
            return forAdmin(pageNo, pageSize, idCode);
        } else {
            return forNormal(queryManagerRelation(pageNo, pageSize, idCode, manager.getManagerId()));
        }
    }

    // 查询所有的id提供者
    private QueryManagedIderResult forAdmin(int pageNo, int pageSize, String idCode) {
        QueryIderOrder order = new QueryIderOrder();
        order.setPageNo(pageNo);
        order.setPageSize(pageSize);
        order.setIdCode(idCode);
        order.setPeriodType(null);
        QueryIderResult queryIderResult = iderManageService.queryIder(order);
        // 构建返回结果
        QueryManagedIderResult result = new QueryManagedIderResult();
        BeanUtils.copyProperties(queryIderResult, result);
        result.getInfos().addAll(queryIderResult.getInfos());
        return result;
    }

    // 查询普通管理员管理的id提供者
    private QueryManagedIderResult forNormal(QueryManagerRelationResult relationResult) {
        QueryManagedIderResult result = new QueryManagedIderResult();
        BeanUtils.copyProperties(relationResult, result, "infos");
        // 根据关系查找id提供者
        for (RelationInfo relationInfo : relationResult.getInfos()) {
            IderInfo iderInfo = findIder(relationInfo.getTargetId());
            if (iderInfo != null) {
                result.addInfo(iderInfo);
            }
        }
        return result;
    }

    // 查找与指定管理员相关的关系
    private QueryManagerRelationResult queryManagerRelation(int pageNo, int pageSize, String idCode, String managerId) {
        QueryManagerRelationOrder order = new QueryManagerRelationOrder();
        order.setPageNo(pageNo);
        order.setPageSize(pageSize);
        order.setManagerId(managerId);
        order.setTargetId(idCode);

        QueryManagerRelationResult result = relationService.queryManagerRelation(order);
        if (!result.isSuccess()) {
            throw new BizException(Status.FAIL, result.getCode(), result.getMessage());
        }
        return result;
    }

    // 查找id提供者
    private IderInfo findIder(String idCode) {
        FindIderOrder order = new FindIderOrder();
        order.setIdCode(idCode);

        FindIderResult result = iderManageService.findIder(order);
        if (!result.isSuccess()) {
            throw new BizException(Status.FAIL, result.getCode(), result.getMessage());
        }
        return result.getIderInfo();
    }

    /**
     * 查找被管理的ider-result
     */
    public static class QueryManagedIderResult extends AbstractQueryResult<IderInfo> {
    }
}
