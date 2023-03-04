/*
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-21 20:56 创建
 */
package org.antframework.idcenter.facade.api;

import org.antframework.common.util.facade.EmptyResult;
import org.antframework.idcenter.facade.order.*;
import org.antframework.idcenter.facade.result.AcquireIdsResult;
import org.antframework.idcenter.facade.result.FindIderResult;
import org.antframework.idcenter.facade.result.QueryIdersResult;

/**
 * id提供者服务
 */
public interface IderService {
    /**
     * 新增id提供者
     */
    EmptyResult addIder(AddIderOrder order);

    /**
     * 修改id提供者的名称
     */
    EmptyResult modifyIderName(ModifyIderNameOrder order);

    /**
     * 修改id提供者的最大数据
     */
    EmptyResult modifyIderMax(ModifyIderMaxOrder order);

    /**
     * 修改id提供者当前数据
     */
    EmptyResult modifyIderCurrent(ModifyIderCurrentOrder order);

    /**
     * 删除id提供者
     */
    EmptyResult deleteIder(DeleteIderOrder order);

    /**
     * 获取批量id
     */
    AcquireIdsResult acquireIds(AcquireIdsOrder order);

    /**
     * 查找id提供者
     */
    FindIderResult findIder(FindIderOrder order);

    /**
     * 查询id提供者
     */
    QueryIdersResult queryIders(QueryIdersOrder order);
}
