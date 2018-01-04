/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-28 20:53 创建
 */
package org.antframework.idcenter.facade.order;

import org.antframework.common.util.facade.AbstractOrder;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 删除id提供者order
 */
public class DeleteIderOrder extends AbstractOrder {
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
