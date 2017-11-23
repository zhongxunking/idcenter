/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-23 22:18 创建
 */
package org.antframework.ids.facade.order;

import org.antframework.common.util.facade.AbstractOrder;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 修改id提供者当前id-order
 */
public class ModifyIderCurrentIdOrder extends AbstractOrder {
    // id编码
    @NotBlank
    private String idCode;
    // 新的当前Id
    @Min(0)
    private long newCurrentId;

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public long getNewCurrentId() {
        return newCurrentId;
    }

    public void setNewCurrentId(long newCurrentId) {
        this.newCurrentId = newCurrentId;
    }
}
