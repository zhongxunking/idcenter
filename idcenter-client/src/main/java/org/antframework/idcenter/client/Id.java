/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-29 16:24 创建
 */
package org.antframework.idcenter.client;

import org.antframework.idcenter.client.core.Period;

/**
 * id
 */
public class Id {
    // id编码
    private String idCode;
    // 周期
    private Period period;
    // id
    private long id;

    public Id(String idCode, Period period, long id) {
        this.idCode = idCode;
        this.period = period;
        this.id = id;
    }

    public String getIdCode() {
        return idCode;
    }

    public Period getPeriod() {
        return period;
    }

    public long getId() {
        return id;
    }
}
