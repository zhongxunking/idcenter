/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-02-01 21:37 创建
 */
package org.antframework.idcenter.facade.order;

import lombok.Getter;
import lombok.Setter;
import org.antframework.common.util.facade.AbstractOrder;

import javax.validation.constraints.NotBlank;

/**
 * 查找id提供者order
 */
@Getter
@Setter
public class FindIderOrder extends AbstractOrder {
    // id提供者的id（id编码）
    @NotBlank
    private String iderId;
}
