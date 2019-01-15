/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-27 20:15 创建
 */
package org.antframework.idcenter.facade.order;

import lombok.Getter;
import lombok.Setter;
import org.antframework.common.util.facade.AbstractQueryOrder;
import org.antframework.common.util.id.PeriodType;
import org.antframework.common.util.query.annotation.operator.QueryEQ;
import org.antframework.common.util.query.annotation.operator.QueryLike;

/**
 * 查询id提供者order
 */
@Getter
@Setter
public class QueryIdersOrder extends AbstractQueryOrder {
    // id提供者的id（id编码）
    @QueryLike
    private String iderId;
    // 周期类型
    @QueryEQ
    private PeriodType periodType;
}
