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
import org.antframework.idcenter.facade.api.IderService;
import org.antframework.idcenter.facade.enums.PeriodType;
import org.antframework.idcenter.facade.order.*;
import org.antframework.idcenter.facade.result.FindIderResult;
import org.antframework.idcenter.facade.result.QueryIderResult;
import org.antframework.idcenter.facade.vo.IderInfo;
import org.antframework.manager.facade.enums.ManagerType;
import org.antframework.manager.facade.info.ManagerInfo;
import org.antframework.manager.facade.info.RelationInfo;
import org.antframework.manager.facade.result.QueryManagerRelationResult;
import org.antframework.manager.web.common.ManagerAssert;
import org.antframework.manager.web.common.Managers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
    private IderService iderService;

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

        return iderService.addIder(order);
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

        return iderService.modifyIderMax(order);
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

        return iderService.modifyIderFactor(order);
    }

    /**
     * 修改id提供者的当前数据
     *
     * @param idCode           id编码（必填）
     * @param newCurrentPeriod 新的当前周期（格式：yyyyMMddHH），无周期则传null
     * @param newCurrentId     新的当前id（必填）
     * @return 修改结果
     */
    @RequestMapping("/modifyCurrent")
    public EmptyResult modifyCurrent(String idCode, @DateTimeFormat(pattern = "yyyyMMddHH") Date newCurrentPeriod, long newCurrentId) {
        ManagerAssert.adminOrHaveRelation(idCode);
        ModifyIderCurrentOrder order = new ModifyIderCurrentOrder();
        order.setIdCode(idCode);
        order.setNewCurrentPeriod(newCurrentPeriod);
        order.setNewCurrentId(newCurrentId);

        return iderService.modifyIderCurrent(order);
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
        Managers.deleteAllRelationsByTarget(idCode);
        // 删除id提供者
        DeleteIderOrder order = new DeleteIderOrder();
        order.setIdCode(idCode);
        return iderService.deleteIder(order);
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
            return forNormal(Managers.queryManagerRelation(pageNo, pageSize, idCode));
        }
    }

    // 查询所有的id提供者
    private QueryManagedIderResult forAdmin(int pageNo, int pageSize, String idCode) {
        QueryIderOrder order = new QueryIderOrder();
        order.setPageNo(pageNo);
        order.setPageSize(pageSize);
        order.setIdCode(idCode);
        order.setPeriodType(null);
        QueryIderResult queryIderResult = iderService.queryIder(order);
        // 构建返回结果
        QueryManagedIderResult result = new QueryManagedIderResult();
        BeanUtils.copyProperties(queryIderResult, result, "infos");
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

    // 查找id提供者
    private IderInfo findIder(String idCode) {
        FindIderOrder order = new FindIderOrder();
        order.setIdCode(idCode);

        FindIderResult result = iderService.findIder(order);
        if (!result.isSuccess()) {
            throw new BizException(Status.FAIL, result.getCode(), result.getMessage());
        }
        return result.getIderInfo();
    }

    /**
     * 查询被管理的id提供者result
     */
    public static class QueryManagedIderResult extends AbstractQueryResult<IderInfo> {
    }
}
