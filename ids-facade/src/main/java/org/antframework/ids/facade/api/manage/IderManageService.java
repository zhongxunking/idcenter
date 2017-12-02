/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-21 20:56 创建
 */
package org.antframework.ids.facade.api.manage;

import org.antframework.ids.facade.order.*;
import org.antframework.ids.facade.result.*;

/**
 * id提供者管理服务
 */
public interface IderManageService {

    /**
     * 新增id提供者
     */
    AddIderResult addIder(AddIderOrder order);

    /**
     * 修改id提供者的最大id
     */
    ModifyIderMaxIdResult modifyIderMaxId(ModifyIderMaxIdOrder order);

    /**
     * 新增或修改id提供者
     */
    AddOrModifyIderResult addOrModifyIder(AddOrModifyIderOrder order);

    /**
     * 修改id提供者的因数
     */
    ModifyIderFactorResult modifyIderFactor(ModifyIderFactorOrder order);

    /**
     * 修改id提供者当前数据
     */
    ModifyIderCurrentResult modifyIderCurrent(ModifyIderCurrentOrder order);

    /**
     * 删除id提供者
     */
    DeleteIderResult deleteIder(DeleteIderOrder order);

    /**
     * 查询id提供者
     */
    QueryIderResult queryIder(QueryIderOrder order);
}
