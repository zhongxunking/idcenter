/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-31 23:39 创建
 */
package org.antframework.idcenter.web.controller.manage;

import org.antframework.common.util.facade.*;
import org.antframework.common.util.id.PeriodType;
import org.antframework.idcenter.biz.util.IderUtils;
import org.antframework.idcenter.facade.api.IderService;
import org.antframework.idcenter.facade.info.IderInfo;
import org.antframework.idcenter.facade.order.*;
import org.antframework.idcenter.facade.result.FindIderResult;
import org.antframework.idcenter.facade.result.QueryIdersResult;
import org.antframework.idcenter.web.common.ManagerIders;
import org.antframework.manager.facade.enums.ManagerType;
import org.antframework.manager.facade.info.ManagerInfo;
import org.antframework.manager.web.Managers;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
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
     * @param iderId     id提供者的id（id编码）
     * @param iderName   id提供者的名称
     * @param periodType 周期类型
     * @param maxId      一个周期内id最大值（不包含），null表示不限制
     * @param maxAmount  单次获取id的最大数量（包含），null表示不限制
     * @return 新增结果
     */
    @RequestMapping("/addIder")
    public EmptyResult addIder(String iderId, String iderName, PeriodType periodType, Long maxId, Integer maxAmount) {
        Managers.admin();
        AddIderOrder order = new AddIderOrder();
        order.setIderId(iderId);
        order.setIderName(iderName);
        order.setPeriodType(periodType);
        order.setMaxId(maxId);
        order.setMaxAmount(maxAmount);

        return iderService.addIder(order);
    }

    /**
     * 修改id提供者的名称
     *
     * @param iderId      id提供者的id（id编码）
     * @param newIderName 新的id提供者的名称
     * @return 修改结果
     */
    @RequestMapping("/modifyIderName")
    public EmptyResult modifyIderName(String iderId, String newIderName) {
        ManagerIders.adminOrHaveIder(iderId);
        ModifyIderNameOrder order = new ModifyIderNameOrder();
        order.setIderId(iderId);
        order.setNewIderName(newIderName);

        return iderService.modifyIderName(order);
    }

    /**
     * 修改id提供者的最大数据
     *
     * @param iderId       id提供者的id（id编码）
     * @param newMaxId     新的一个周期内id最大值（不包含），null表示不限制
     * @param newMaxAmount 新的单次获取id的最大数量（包含），null表示不限制
     * @return 修改结果
     */
    @RequestMapping("/modifyIderMax")
    public EmptyResult modifyIderMax(String iderId, Long newMaxId, Integer newMaxAmount) {
        ManagerIders.adminOrHaveIder(iderId);
        ModifyIderMaxOrder order = new ModifyIderMaxOrder();
        order.setIderId(iderId);
        order.setNewMaxId(newMaxId);
        order.setNewMaxAmount(newMaxAmount);

        return iderService.modifyIderMax(order);
    }

    /**
     * 修改id提供者的因数
     *
     * @param iderId    id提供者的id（id编码）
     * @param newFactor 新的因数（生产者数量）
     * @return 修改结果
     */
    @RequestMapping("/modifyIderFactor")
    public EmptyResult modifyIderFactor(String iderId, Integer newFactor) {
        ManagerIders.adminOrHaveIder(iderId);
        ModifyIderFactorOrder order = new ModifyIderFactorOrder();
        order.setIderId(iderId);
        order.setNewFactor(newFactor);

        return iderService.modifyIderFactor(order);
    }

    /**
     * 修改id提供者的当前数据
     *
     * @param iderId           id提供者的id（id编码）
     * @param newCurrentPeriod 新的当前周期
     * @param newCurrentId     新的当前Id（未使用）
     * @return 修改结果
     */
    @RequestMapping("/modifyIderCurrent")
    public EmptyResult modifyIderCurrent(String iderId, String newCurrentPeriod, Long newCurrentId) {
        ManagerIders.adminOrHaveIder(iderId);
        // 解析出新的当前周期
        IderInfo ider = IderUtils.findIder(iderId);
        if (ider == null) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", iderId));
        }
        Date newCurrentPeriodDate = parseCurrentPeriod(ider.getPeriodType(), newCurrentPeriod);
        // 修改当前数据
        ModifyIderCurrentOrder order = new ModifyIderCurrentOrder();
        order.setIderId(iderId);
        order.setNewCurrentPeriod(newCurrentPeriodDate);
        order.setNewCurrentId(newCurrentId);

        return iderService.modifyIderCurrent(order);
    }

    // 解析出当前周期
    private Date parseCurrentPeriod(PeriodType periodType, String periodStr) {
        String pattern;
        switch (periodType) {
            case HOUR:
                pattern = "yyyyMMddHH";
                break;
            case DAY:
                pattern = "yyyyMMdd";
                break;
            case MONTH:
                pattern = "yyyyMM";
                break;
            case YEAR:
                pattern = "yyyy";
                break;
            case NONE:
                return null;
            default:
                throw new IllegalStateException("无法识别的周期类型：" + periodType);
        }
        if (periodStr.length() != pattern.length()) {
            throw new IllegalArgumentException("输入的当前周期长度不合法");
        }
        try {
            return DateUtils.parseDate(periodStr, pattern);
        } catch (ParseException e) {
            return ExceptionUtils.rethrow(e);
        }
    }

    /**
     * 删除id提供者
     *
     * @param iderId id提供者的id（id编码）
     * @return 删除结果
     */
    @RequestMapping("/deleteIder")
    public EmptyResult deleteIder(String iderId) {
        Managers.admin();
        // 删除与该id提供者有关的管理权限
        ManagerIders.deletesByIder(iderId);

        DeleteIderOrder order = new DeleteIderOrder();
        order.setIderId(iderId);

        return iderService.deleteIder(order);
    }

    /**
     * 查找id提供者
     *
     * @param iderId id提供者的id（id编码）
     * @return 查找结果
     */
    @RequestMapping("/findIder")
    public FindIderResult findIder(String iderId) {
        ManagerIders.adminOrHaveIder(iderId);
        FindIderOrder order = new FindIderOrder();
        order.setIderId(iderId);

        return iderService.findIder(order);
    }

    /**
     * 查询被管理的id提供者
     *
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @param iderId   id提供者的id（id编码）
     * @return 查询结果
     */
    @RequestMapping("/queryManagedIders")
    public QueryManagedIdersResult queryManagedIders(int pageNo, int pageSize, String iderId) {
        ManagerInfo manager = Managers.currentManager();
        if (manager.getType() == ManagerType.ADMIN) {
            return forAdmin(pageNo, pageSize, iderId);
        } else {
            return forNormal(ManagerIders.queryManagedIders(pageNo, pageSize, iderId));
        }
    }

    // 查询所有的id提供者
    private QueryManagedIdersResult forAdmin(int pageNo, int pageSize, String iderId) {
        QueryIdersOrder order = new QueryIdersOrder();
        order.setPageNo(pageNo);
        order.setPageSize(pageSize);
        order.setIderId(iderId);
        order.setPeriodType(null);
        QueryIdersResult queryIdersResult = iderService.queryIders(order);
        // 构建返回结果
        QueryManagedIdersResult result = new QueryManagedIdersResult();
        BeanUtils.copyProperties(queryIdersResult, result, "infos");
        result.getInfos().addAll(queryIdersResult.getInfos());
        return result;
    }

    // 查询普通管理员管理的id提供者
    private QueryManagedIdersResult forNormal(AbstractQueryResult<String> iderIdsResult) {
        QueryManagedIdersResult result = new QueryManagedIdersResult();
        BeanUtils.copyProperties(iderIdsResult, result, "infos");
        // 根据关系查找id提供者
        for (String iderId : iderIdsResult.getInfos()) {
            FindIderResult findIderResult = findIder(iderId);
            FacadeUtils.assertSuccess(findIderResult);
            if (findIderResult.getIder() != null) {
                result.addInfo(findIderResult.getIder());
            }
        }
        return result;
    }

    /**
     * 查询被管理的id提供者result
     */
    public static class QueryManagedIdersResult extends AbstractQueryResult<IderInfo> {
    }
}
