/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-23 22:04 创建
 */
package org.antframework.ids.facade.order;

import org.antframework.common.util.facade.AbstractOrder;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 修改id提供者当前周期order
 */
public class ModifyIderCurrentPeriodOrder extends AbstractOrder {
    // id编码
    @NotBlank
    private String idCode;
    // 新的当前周期
    @NotNull
    private Date newCurrentPeriod;

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public Date getNewCurrentPeriod() {
        return newCurrentPeriod;
    }

    public void setNewCurrentPeriod(Date newCurrentPeriod) {
        this.newCurrentPeriod = newCurrentPeriod;
    }
}
