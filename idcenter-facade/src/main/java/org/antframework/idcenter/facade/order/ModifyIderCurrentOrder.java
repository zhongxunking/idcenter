/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-27 23:09 创建
 */
package org.antframework.idcenter.facade.order;

import lombok.Getter;
import lombok.Setter;
import org.antframework.common.util.facade.AbstractOrder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 修改id提供者当前数据Order
 */
@Getter
@Setter
public class ModifyIderCurrentOrder extends AbstractOrder {
    // id提供者的id（id编码）
    @NotBlank
    private String iderId;
    // 新的当前周期（null表示无周期）
    private Date newCurrentPeriod;
    // 新的当前Id（未使用）
    @Min(0)
    @NotNull
    private Long newCurrentId;
}
