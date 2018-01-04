/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-21 00:42 创建
 */
package org.antframework.idcenter.dal.entity;

import org.antframework.boot.jpa.AbstractEntity;
import org.antframework.idcenter.facade.enums.PeriodType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * id提供者
 */
@Entity
public class Ider extends AbstractEntity {
    // id编码
    @Column(unique = true, length = 128)
    private String idCode;

    // 周期类型
    @Column(length = 40)
    @Enumerated(EnumType.STRING)
    private PeriodType periodType;

    // 一个周期内id最大值（不包含），null表示不限制
    @Column
    private Long maxId;

    // 一次获取id的最大数量（包含），null表示不限制
    @Column
    private Integer maxAmount;

    // 因数（生产者数量）
    @Column
    private Integer factor;

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    public Integer getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Integer maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Integer getFactor() {
        return factor;
    }

    public void setFactor(Integer factor) {
        this.factor = factor;
    }
}
