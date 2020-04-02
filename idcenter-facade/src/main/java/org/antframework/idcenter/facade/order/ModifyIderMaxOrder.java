/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-05 23:43 创建
 */
package org.antframework.idcenter.facade.order;

import lombok.Getter;
import lombok.Setter;
import org.antframework.common.util.facade.AbstractOrder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 修改id提供者的最大数据order
 */
@Getter
@Setter
public class ModifyIderMaxOrder extends AbstractOrder {
    // id提供者的id（id编码）
    @NotBlank
    private String iderId;
    // 新的一个周期内id最大值（不包含），null表示不限制
    @Min(1)
    private Long newMaxId;
    // 新的单次获取id的最大数量（包含），null表示不限制
    @Min(0)
    private Integer newMaxAmount;
}
