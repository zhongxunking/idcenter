/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-29 22:48 创建
 */
package org.antframework.ids.facade.info;

import org.antframework.common.util.facade.AbstractInfo;

import java.util.Date;

/**
 * 批量id-info
 */
public class IdsInfo extends AbstractInfo {
    // 因数
    private int factor;
    // 周期
    private Date period;
    // 开始id
    private long startId;
    // id个数
    private int amount;

    public int getFactor() {
        return factor;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

    public Date getPeriod() {
        return period;
    }

    public void setPeriod(Date period) {
        this.period = period;
    }

    public long getStartId() {
        return startId;
    }

    public void setStartId(long startId) {
        this.startId = startId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
