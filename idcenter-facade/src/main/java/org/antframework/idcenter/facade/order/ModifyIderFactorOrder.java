/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-25 19:56 创建
 */
package org.antframework.idcenter.facade.order;

import lombok.Getter;
import lombok.Setter;
import org.antframework.common.util.facade.AbstractOrder;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 修改id提供者的因数order
 */
@Getter
@Setter
public class ModifyIderFactorOrder extends AbstractOrder {
    // id提供者的id（id编码）
    @NotBlank
    private String iderId;
    // 新的因数（生产者数量）
    @Min(1)
    @NotNull
    private Integer newFactor;
}
