/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-27 20:15 创建
 */
package org.antframework.idcenter.facade.order;

import org.antframework.common.util.facade.AbstractQueryOrder;
import org.antframework.common.util.query.annotation.operator.QueryEQ;
import org.antframework.common.util.query.annotation.operator.QueryLike;
import org.antframework.idcenter.facade.enums.PeriodType;

/**
 * 查询id提供者order
 */
public class QueryIdersOrder extends AbstractQueryOrder {
    // id编码
    @QueryLike
    private String idCode;
    // 周期类型
    @QueryEQ
    private PeriodType periodType;

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }
}
