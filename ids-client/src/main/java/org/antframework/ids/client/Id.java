/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-29 16:24 创建
 */
package org.antframework.ids.client;

import org.antframework.ids.client.core.PeriodType;

import java.util.Date;

/**
 * id
 */
public class Id {
    // id编码
    private String idCode;
    // 周期类型
    private PeriodType periodType;
    // 周期
    private Date period;
    // id
    private long id;

    public Id(String idCode, PeriodType periodType, Date period, long id) {
        this.idCode = idCode;
        this.periodType = periodType;
        this.period = period;
        this.id = id;
    }

    public String getIdCode() {
        return idCode;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public Date getPeriod() {
        return period;
    }

    public long getId() {
        return id;
    }
}
