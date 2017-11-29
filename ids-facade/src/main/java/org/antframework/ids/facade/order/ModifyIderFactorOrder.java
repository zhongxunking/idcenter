/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-25 19:56 创建
 */
package org.antframework.ids.facade.order;

import org.antframework.common.util.facade.AbstractOrder;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 修改id提供者的因数order
 */
public class ModifyIderFactorOrder extends AbstractOrder {
    // id编码
    @NotBlank
    private String idCode;
    // 新的因数
    @Min(1)
    private int newFactor;

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public int getNewFactor() {
        return newFactor;
    }

    public void setNewFactor(int newFactor) {
        this.newFactor = newFactor;
    }
}
