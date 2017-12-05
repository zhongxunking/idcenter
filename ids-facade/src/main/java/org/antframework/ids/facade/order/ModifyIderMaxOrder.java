/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-05 23:43 创建
 */
package org.antframework.ids.facade.order;

import org.antframework.common.util.facade.AbstractOrder;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 修改id提供者的最大数据order
 */
public class ModifyIderMaxOrder extends AbstractOrder {
    // id编码
    @NotBlank
    private String idCode;
    // 新的id最大值
    @Min(1)
    private Long newMaxId;
    // 新的一次获取id的最大数量
    @Min(1)
    private Integer newMaxAmount;

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

    public Integer getNewMaxAmount() {
        return newMaxAmount;
    }

    public void setNewMaxAmount(Integer newMaxAmount) {
        this.newMaxAmount = newMaxAmount;
    }
}
