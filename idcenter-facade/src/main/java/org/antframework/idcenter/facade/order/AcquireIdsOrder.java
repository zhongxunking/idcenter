/*
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-29 22:41 创建
 */
package org.antframework.idcenter.facade.order;

import lombok.Getter;
import lombok.Setter;
import org.antframework.common.util.facade.AbstractOrder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 获取批量id-order
 */
@Getter
@Setter
public class AcquireIdsOrder extends AbstractOrder {
    // id提供者的id（id编码）
    @NotBlank
    private String iderId;
    // 数量
    @Min(0)
    @NotNull
    private Integer amount;
}
