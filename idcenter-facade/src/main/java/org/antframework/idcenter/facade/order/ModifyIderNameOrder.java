/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-17 23:18 创建
 */
package org.antframework.idcenter.facade.order;

import lombok.Getter;
import lombok.Setter;
import org.antframework.common.util.facade.AbstractOrder;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 修改id提供者的名称
 */
@Getter
@Setter
public class ModifyIderNameOrder extends AbstractOrder {
    // id提供者的id（id编码）
    @NotBlank
    private String iderId;
    // 新的id提供者的名称
    private String newIderName;
}
