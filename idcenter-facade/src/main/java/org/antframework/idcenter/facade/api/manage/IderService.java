/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-21 20:56 创建
 */
package org.antframework.idcenter.facade.api.manage;

import org.antframework.common.util.facade.EmptyResult;
import org.antframework.idcenter.facade.order.*;
import org.antframework.idcenter.facade.result.FindIderResult;
import org.antframework.idcenter.facade.result.QueryIderResult;

/**
 * id提供者服务
 */
public interface IderService {

    /**
     * 新增id提供者
     */
    EmptyResult addIder(AddIderOrder order);

    /**
     * 修改id提供者的最大数据
     */
    EmptyResult modifyIderMax(ModifyIderMaxOrder order);

    /**
     * 修改id提供者的因数
     */
    EmptyResult modifyIderFactor(ModifyIderFactorOrder order);

    /**
     * 修改id提供者当前数据
     */
    EmptyResult modifyIderCurrent(ModifyIderCurrentOrder order);

    /**
     * 删除id提供者
     */
    EmptyResult deleteIder(DeleteIderOrder order);

    /**
     * 查找id提供者
     */
    FindIderResult findIder(FindIderOrder order);

    /**
     * 查询id提供者
     */
    QueryIderResult queryIder(QueryIderOrder order);
}
