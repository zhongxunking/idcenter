/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-02 13:49 创建
 */
package org.antframework.ids.facade.order;

import org.antframework.common.util.facade.AbstractOrder;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 修改id提供者的最大id-order
 */
public class ModifyIderMaxIdOrder extends AbstractOrder {
    // id编码
    @NotBlank
    private String idCode;
    // 新的id最大值
    @Min(2)
    private Long newMaxId;

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public Long getNewMaxId() {
        return newMaxId;
    }

    public void setNewMaxId(Long newMaxId) {
        this.newMaxId = newMaxId;
    }
}
