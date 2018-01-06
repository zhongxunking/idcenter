/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-29 22:48 创建
 */
package org.antframework.idcenter.client.core;

import org.antframework.idcenter.client.Id;

import java.util.Date;

/**
 * 批量id
 */
public class Ids {
    // id编码
    private String idCode;
    // 周期
    private Period period;
    // 因数
    private int factor;
    // 开始id（包含）
    private long startId;
    // id个数
    private int amount;

    public Ids(String idCode, Period period, int factor, long startId, int amount) {
        this.idCode = idCode;
        this.period = period;
        this.factor = factor;
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

        Id id = new Id(idCode, period, startId);

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
        Period modernPeriod = new Period(period.getType(), new Date(System.currentTimeMillis() - periodError));
        if (period.compareTo(modernPeriod) < 0) {
            return 0;
        }
        return amount;
    }
}
