/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-02-01 21:37 创建
 */
package org.antframework.idcenter.facade.order;

import org.antframework.common.util.facade.AbstractOrder;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 查找id提供者order
 */
public class FindIderOrder extends AbstractOrder {
    // id编码
    @NotBlank
    private String idCode;

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }
}
