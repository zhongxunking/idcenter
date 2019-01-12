/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-25 17:17 创建
 */
package org.antframework.idcenter.dal.entity;

import lombok.Getter;
import lombok.Setter;
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
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_iderId_index", columnNames = {"iderId", "index"}))
@Getter
@Setter
public class IdProducer extends AbstractEntity {
    // id提供者的id（id编码）
    @Column(length = 128)
    private String iderId;

    // 序号
    @Column(name = "`index`")
    private Integer index;

    // 当前周期
    @Column
    private Date currentPeriod;

    // 当前Id（未使用）
    @Column
    private Long currentId;
}
