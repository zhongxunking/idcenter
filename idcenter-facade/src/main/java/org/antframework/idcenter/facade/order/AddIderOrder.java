/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-02 13:29 创建
 */
package org.antframework.idcenter.facade.order;

import lombok.Getter;
import lombok.Setter;
import org.antframework.common.util.facade.AbstractOrder;
import org.antframework.common.util.id.PeriodType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 新增id提供者order
 */
@Getter
@Setter
public class AddIderOrder extends AbstractOrder {
    // id提供者的id（id编码）
    @NotBlank
    private String iderId;
    // id提供者的名称
    private String iderName;
    // 周期类型
    @NotNull
    private PeriodType periodType;
    // 一个周期内id最大值（不包含），null表示不限制
    @Min(1)
    private Long maxId;
    // 单次获取id的最大数量（包含），null表示不限制
    @Min(0)
    private Integer maxAmount;
}
