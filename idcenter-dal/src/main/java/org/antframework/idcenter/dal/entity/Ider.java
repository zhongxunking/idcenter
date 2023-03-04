/*
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-21 00:42 创建
 */
package org.antframework.idcenter.dal.entity;

import lombok.Getter;
import lombok.Setter;
import org.antframework.boot.jpa.AbstractEntity;
import org.antframework.common.util.id.PeriodType;

import javax.persistence.*;
import java.util.Date;

/**
 * id提供者
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_iderId", columnNames = "iderId"))
@Getter
@Setter
public class Ider extends AbstractEntity {
    // id提供者的id（id编码）
    @Column(length = 128)
    private String iderId;

    // id提供者的名称
    @Column
    private String iderName;

    // 单次获取id的最大数量（包含），null表示不限制
    @Column
    private Integer maxAmount;

    // 周期类型
    @Column(length = 64)
    @Enumerated(EnumType.STRING)
    private PeriodType periodType;

    // 一个周期内id最大值（不包含），null表示不限制
    @Column
    private Long maxId;

    // 当前周期
    @Column
    private Date currentPeriod;

    // 当前Id（还未被使用）
    @Column
    private Long currentId;
}
