/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-25 17:17 创建
 */
package org.antframework.ids.dal.entity;

import org.antframework.boot.jpa.AbstractEntity;

import javax.persistence.Column;
import java.util.Date;

/**
 * id生产者
 */
public class Producer extends AbstractEntity {
    // 生产者编码
    @Column(unique = true, length = 128)
    private String producerCode;

    // 当前周期
    @Column
    private Date currentPeriod;

    // 当前Id
    @Column
    private Long currentId;

    public String getProducerCode() {
        return producerCode;
    }

    public void setProducerCode(String producerCode) {
        this.producerCode = producerCode;
    }

    public Date getCurrentPeriod() {
        return currentPeriod;
    }

    public void setCurrentPeriod(Date currentPeriod) {
        this.currentPeriod = currentPeriod;
    }

    public Long getCurrentId() {
        return currentId;
    }

    public void setCurrentId(Long currentId) {
        this.currentId = currentId;
    }
}
