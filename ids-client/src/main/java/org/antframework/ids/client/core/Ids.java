/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-29 22:48 创建
 */
package org.antframework.ids.client.core;

import org.antframework.ids.client.Id;

import java.util.Date;

/**
 * 批量id
 */
public class Ids {
    // id编码
    private String idCode;
    // 周期类型
    private PeriodType periodType;
    // 因数
    private int factor;
    // 周期
    private Date period;
    // 开始id
    private long startId;
    // id个数
    private int amount;

    public Ids(String idCode, PeriodType periodType, int factor, Date period, long startId, int amount) {
        this.idCode = idCode;
        this.periodType = periodType;
        this.factor = factor;
        this.period = period;
        this.startId = startId;
        this.amount = amount;
    }

    /**
     * 获取一个id
     *
     * @param periodError 允许的周期误差
     * @return null 如果不存在有效id
     */
    public Id getId(long periodError) {
        if (getAmount(periodError) <= 0) {
            return null;
        }

        Id id = new Id(idCode, periodType, period, startId);

        startId += factor;
        amount--;

        return id;
    }

    /**
     * 获取id个数
     *
     * @param periodError 允许的周期误差
     * @return id个数
     */
    public int getAmount(long periodError) {
        Date modernPeriod = PeriodUtils.parse(periodType, new Date(System.currentTimeMillis() - periodError));
        if (PeriodUtils.compare(period, modernPeriod) < 0) {
            return 0;
        }
        return amount;
    }
}
