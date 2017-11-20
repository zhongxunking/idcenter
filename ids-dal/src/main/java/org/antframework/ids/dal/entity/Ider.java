/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-21 00:42 创建
 */
package org.antframework.ids.dal.entity;

import org.antframework.boot.jpa.AbstractEntity;
import org.antframework.ids.facade.enums.PeriodType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

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

    // 当前周期
    @Column
    private Date currentPeriod;

    // 一个周期内id最大值（不包含）
    @Column
    private Long maxId;

    // 当前Id
    @Column
    private Long currentId;

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

    public Date getCurrentPeriod() {
        return currentPeriod;
    }

    public void setCurrentPeriod(Date currentPeriod) {
        this.currentPeriod = currentPeriod;
    }

    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    public Long getCurrentId() {
        return currentId;
    }

    public void setCurrentId(Long currentId) {
        this.currentId = currentId;
    }
}
