/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-27 23:09 创建
 */
package org.antframework.idcenter.facade.order;

import org.antframework.common.util.facade.AbstractOrder;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * 修改id提供者当前数据Order
 */
public class ModifyIderCurrentOrder extends AbstractOrder {
    // id编码
    @NotBlank
    private String idCode;
    // 新的当前周期
    private Date newCurrentPeriod;
    // 新的当前Id
    @Min(0)
    private long newCurrentId;

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

    public long getNewCurrentId() {
        return newCurrentId;
    }

    public void setNewCurrentId(long newCurrentId) {
        this.newCurrentId = newCurrentId;
    }
}
