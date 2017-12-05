/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-02 13:29 创建
 */
package org.antframework.ids.facade.order;

import org.antframework.common.util.facade.AbstractOrder;
import org.antframework.ids.facade.enums.PeriodType;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 新增id提供者order
 */
public class AddIderOrder extends AbstractOrder {
    // id编码
    @NotBlank
    private String idCode;
    // 周期类型
    @NotNull
    private PeriodType periodType;
    // 一个周期内id最大值（不包含），null表示不限制最大值
    @Min(1)
    private Long maxId;

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

    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }
}
