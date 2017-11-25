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
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;

/**
 * id生产者
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"idCode", "index"}))
public class Producer extends AbstractEntity {
    // id编码
    @Column(length = 128)
    private String idCode;

    // 序号
    @Column(name = "`index`")
    private Integer index;

    // 当前周期
    @Column
    private Date currentPeriod;

    // 当前Id
    @Column
    private Long currentId;

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
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
