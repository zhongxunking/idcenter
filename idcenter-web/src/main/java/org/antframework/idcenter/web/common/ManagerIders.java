/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-17 22:06 创建
 */
package org.antframework.idcenter.web.common;

import org.antframework.boot.core.Contexts;
import org.antframework.common.util.facade.*;
import org.antframework.manager.facade.api.RelationService;
import org.antframework.manager.facade.enums.ManagerType;
import org.antframework.manager.facade.info.ManagerInfo;
import org.antframework.manager.facade.info.RelationInfo;
import org.antframework.manager.facade.order.QuerySourceRelationsOrder;
import org.antframework.manager.facade.result.QuerySourceRelationsResult;
import org.antframework.manager.web.Managers;
import org.antframework.manager.web.Relations;
import org.springframework.beans.BeanUtils;

/**
 * 管理员与id提供者关系的工具类
 */
public final class ManagerIders {
    // 关系类型
    private static final String RELATION_TYPE = "manager-app";
    // 关系服务
    private static final RelationService RELATION_SERVICE = Contexts.getApplicationContext().getBean(RelationService.class);

    /**
     * 断言当前管理员为超级管理员或管理着指定id提供者
     *
     * @param iderId id提供者的id
     */
    public static void adminOrHaveIder(String iderId) {
        ManagerInfo manager = Managers.currentManager();
        if (manager.getType() == ManagerType.ADMIN) {
            return;
        }
        RelationInfo relation = Relations.findRelation(RELATION_TYPE, manager.getManagerId(), iderId);
        if (relation == null) {
            throw new BizException(Status.FAIL, CommonResultCode.ILLEGAL_STATE.getCode(), CommonResultCode.ILLEGAL_STATE.getMessage());
        }
    }

    /**
     * 删除与指定id提供者有关的管理权限
     *
     * @param iderId id提供者的id
     */
    public static void deletesByIder(String iderId) {
        Relations.deleteRelations(RELATION_TYPE, null, iderId);
    }

    /**
     * 查找管理员管理的id提供者
     *
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @param iderId   id提供者的id（id编码）
     * @return 被管理的id提供者
     */
    public static AbstractQueryResult<String> queryManagedIders(int pageNo, int pageSize, String iderId) {
        QuerySourceRelationsOrder order = new QuerySourceRelationsOrder();
        order.setPageNo(pageNo);
        order.setPageSize(pageSize);
        order.setType(RELATION_TYPE);
        order.setSource(Managers.currentManager().getManagerId());
        order.setTarget(iderId);

        QuerySourceRelationsResult relationsResult = RELATION_SERVICE.querySourceRelations(order);
        FacadeUtils.assertSuccess(relationsResult);

        AbstractQueryResult<String> result = new AbstractQueryResult<String>() {
        };
        BeanUtils.copyProperties(relationsResult, result, "infos");
        for (RelationInfo relation : relationsResult.getInfos()) {
            result.addInfo(relation.getTarget());
        }

        return result;
    }
}
