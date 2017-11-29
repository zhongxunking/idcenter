/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-29 22:41 创建
 */
package org.antframework.ids.facade.order;

import org.antframework.common.util.facade.AbstractOrder;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 获取批量id-order
 */
public class AcquireIdsOrder extends AbstractOrder {
    // id编码
    @NotBlank
    private String idCode;
    // 期望获取到的id个数
    @Min(1)
    private int expectAmount;

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public int getExpectAmount() {
        return expectAmount;
    }

    public void setExpectAmount(int expectAmount) {
        this.expectAmount = expectAmount;
    }
}
