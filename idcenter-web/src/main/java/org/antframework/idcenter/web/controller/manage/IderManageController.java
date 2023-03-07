/*
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-31 23:39 创建
 */
package org.antframework.idcenter.web.controller.manage;

import lombok.AllArgsConstructor;
import org.antframework.common.util.facade.*;
import org.antframework.common.util.id.PeriodType;
import org.antframework.datasource.DataSourceTemplate;
import org.antframework.idcenter.biz.util.Iders;
import org.antframework.idcenter.facade.api.IderService;
import org.antframework.idcenter.facade.info.IderInfo;
import org.antframework.idcenter.facade.order.*;
import org.antframework.idcenter.facade.result.FindIderResult;
import org.antframework.idcenter.facade.result.QueryIdersResult;
import org.antframework.idcenter.web.common.ManagerIders;
import org.antframework.idcenter.web.idshard.IdShardHub;
import org.antframework.manager.facade.enums.ManagerType;
import org.antframework.manager.facade.info.ManagerInfo;
import org.antframework.manager.web.CurrentManagerAssert;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * id提供者管理controller
 */
@RestController
@RequestMapping("/manage/ider")
@AllArgsConstructor
public class IderManageController {
    // id分片中心
    private final IdShardHub idShardHub;
    // 数据源模板
    private final DataSourceTemplate dataSourceTemplate;
    // id提供者服务
    private final IderService iderService;

    /**
     * 新增id提供者
     *
     * @param dataSource 数据源
     * @param iderId     id提供者的id（id编码）
     * @param iderName   id提供者的名称
     * @param periodType 周期类型
     * @param maxId      一个周期内id最大值（不包含），null表示不限制
     * @param maxAmount  单次获取id的最大数量（包含），null表示不限制
     * @return 新增结果
     */
    @RequestMapping("/addIder")
    public EmptyResult addIder(String dataSource, String iderId, String iderName, PeriodType periodType, Long maxId, Integer maxAmount) {
        CurrentManagerAssert.admin();

        AddIderOrder order = new AddIderOrder();
        order.setIderId(iderId);
        order.setIderName(iderName);
        order.setPeriodType(periodType);
        order.setMaxId(maxId);
        order.setMaxAmount(maxAmount);
        return doWithDataSource(dataSource, () -> iderService.addIder(order));
    }

    /**
     * 修改id提供者的名称
     *
     * @param dataSource  数据源
     * @param iderId      id提供者的id（id编码）
     * @param newIderName 新的id提供者的名称
     * @return 修改结果
     */
    @RequestMapping("/modifyIderName")
    public EmptyResult modifyIderName(String dataSource, String iderId, String newIderName) {
        ManagerIders.assertAdminOrHaveIder(iderId);

        ModifyIderNameOrder order = new ModifyIderNameOrder();
        order.setIderId(iderId);
        order.setNewIderName(newIderName);
        return doWithDataSource(dataSource, () -> iderService.modifyIderName(order));
    }

    /**
     * 修改id提供者的最大数据
     *
     * @param dataSource   数据源
     * @param iderId       id提供者的id（id编码）
     * @param newMaxId     新的一个周期内id最大值（不包含），null表示不限制
     * @param newMaxAmount 新的单次获取id的最大数量（包含），null表示不限制
     * @return 修改结果
     */
    @RequestMapping("/modifyIderMax")
    public EmptyResult modifyIderMax(String dataSource, String iderId, Long newMaxId, Integer newMaxAmount) {
        ManagerIders.assertAdminOrHaveIder(iderId);

        ModifyIderMaxOrder order = new ModifyIderMaxOrder();
        order.setIderId(iderId);
        order.setNewMaxId(newMaxId);
        order.setNewMaxAmount(newMaxAmount);
        return doWithDataSource(dataSource, () -> iderService.modifyIderMax(order));
    }

    /**
     * 修改id提供者的当前数据
     *
     * @param dataSource       数据源
     * @param iderId           id提供者的id（id编码）
     * @param newCurrentPeriod 新的当前周期
     * @param newCurrentId     新的当前Id（未使用）
     * @return 修改结果
     */
    @RequestMapping("/modifyIderCurrent")
    public EmptyResult modifyIderCurrent(String dataSource, String iderId, String newCurrentPeriod, Long newCurrentId) {
        ManagerIders.assertAdminOrHaveIder(iderId);

        // 解析出新的当前周期
        IderInfo ider = doWithDataSource(dataSource, () -> Iders.findIder(iderId));
        if (ider == null) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", iderId));
        }
        Date newCurrentPeriodDate = parseCurrentPeriod(ider.getPeriodType(), newCurrentPeriod);
        // 修改当前数据
        ModifyIderCurrentOrder order = new ModifyIderCurrentOrder();
        order.setIderId(iderId);
        order.setNewCurrentPeriod(newCurrentPeriodDate);
        order.setNewCurrentId(newCurrentId);

        return doWithDataSource(dataSource, () -> iderService.modifyIderCurrent(order));
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
     * @param dataSource 数据源
     * @param iderId     id提供者的id（id编码）
     * @return 删除结果
     */
    @RequestMapping("/deleteIder")
    public EmptyResult deleteIder(String dataSource, String iderId) {
        CurrentManagerAssert.admin();
        // 删除与该id提供者有关的管理权限
        ManagerIders.deletesByIder(iderId);

        DeleteIderOrder order = new DeleteIderOrder();
        order.setIderId(iderId);
        return doWithDataSource(dataSource, () -> iderService.deleteIder(order));
    }

    /**
     * 查找id提供者
     *
     * @param dataSource 数据源
     * @param iderId     id提供者的id（id编码）
     * @return 查找结果
     */
    @RequestMapping("/findIder")
    public FindIderResult findIder(String dataSource, String iderId) {
        ManagerIders.assertAdminOrHaveIder(iderId);

        FindIderOrder order = new FindIderOrder();
        order.setIderId(iderId);
        return doWithDataSource(dataSource, () -> iderService.findIder(order));
    }

    /**
     * 查询id提供者
     *
     * @param dataSource 数据源
     * @param pageNo     页码
     * @param pageSize   每页大小
     * @param iderId     id提供者的id（id编码）
     * @param periodType 周期类型
     * @return 查询结果
     */
    @RequestMapping("/queryIders")
    public QueryIdersResult queryIders(String dataSource, int pageNo, int pageSize, String iderId, PeriodType periodType) {
        CurrentManagerAssert.admin();

        QueryIdersOrder order = new QueryIdersOrder();
        order.setPageNo(pageNo);
        order.setPageSize(pageSize);
        order.setIderId(iderId);
        order.setPeriodType(periodType);
        return doWithDataSource(dataSource, () -> iderService.queryIders(order));
    }

    /**
     * 查询被管理的id提供者
     *
     * @param dataSource 数据源
     * @param pageNo     页码
     * @param pageSize   每页大小
     * @param iderId     id提供者的id（id编码）
     * @return 查询结果
     */
    @RequestMapping("/queryManagedIders")
    public QueryManagedIdersResult queryManagedIders(String dataSource, int pageNo, int pageSize, String iderId) {
        ManagerInfo manager = CurrentManagerAssert.current();
        if (manager.getType() == ManagerType.ADMIN) {
            return forAdmin(dataSource, pageNo, pageSize, iderId);
        } else {
            return forNormal(dataSource, ManagerIders.queryManagedIders(pageNo, pageSize, iderId));
        }
    }

    // 查询所有的id提供者
    private QueryManagedIdersResult forAdmin(String dataSource, int pageNo, int pageSize, String iderId) {
        QueryIdersResult queryIdersResult = queryIders(dataSource, pageNo, pageSize, iderId, null);
        // 构建返回结果
        QueryManagedIdersResult result = new QueryManagedIdersResult();
        BeanUtils.copyProperties(queryIdersResult, result, "infos");
        result.getInfos().addAll(queryIdersResult.getInfos());
        return result;
    }

    // 查询普通管理员管理的id提供者
    private QueryManagedIdersResult forNormal(String dataSource, ManagerIders.QueryManagedIdersResult iderIdsResult) {
        QueryManagedIdersResult result = new QueryManagedIdersResult();
        BeanUtils.copyProperties(iderIdsResult, result, "infos");
        // 根据关系查找id提供者
        for (String iderId : iderIdsResult.getInfos()) {
            IderInfo ider = doWithDataSource(dataSource, () -> Iders.findIder(iderId));
            if (ider != null) {
                result.addInfo(ider);
            }
        }
        return result;
    }

    // 指定数据源执行
    private <T> T doWithDataSource(String dataSource, Callable<T> callback) {
        if (!idShardHub.getDataSources().contains(dataSource)) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("数据源[%s]不存在", dataSource));
        }
        return dataSourceTemplate.doWith(dataSource, callback);
    }

    /**
     * 查询被管理的id提供者result
     */
    public static class QueryManagedIdersResult extends AbstractQueryResult<IderInfo> {
    }
}
