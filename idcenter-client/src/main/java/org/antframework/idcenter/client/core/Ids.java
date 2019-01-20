/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-29 22:48 创建
 */
package org.antframework.idcenter.client.core;

import lombok.AllArgsConstructor;
import org.antframework.common.util.id.Id;
import org.antframework.common.util.id.Period;

import java.util.Date;

/**
 * 批量id
 */
@AllArgsConstructor
public class Ids {
    // 周期
    private final Period period;
    // 因数
    private final int factor;
    // 开始id（包含）
    private volatile long startId;
    // id个数
    private volatile int amount;

    /**
     * 获取一个id
     *
     * @param periodError 允许的周期误差（单位：毫秒。null表示忽略周期是否过期）
     * @return id（null表示不存在有效id）
     */
    public Id getId(Long periodError) {
        if (getAmount(periodError) <= 0) {
            return null;
        }

        Id id = new Id(period, startId);

        startId += factor;
        amount--;

        return id;
    }

    /**
     * 获取id个数
     *
     * @param periodError 允许的周期误差（单位：毫秒。null表示忽略周期是否过期）
     * @return id个数
     */
    public int getAmount(Long periodError) {
        if (periodError != null) {
            Period modernPeriod = new Period(period.getType(), new Date(System.currentTimeMillis() - periodError));
            if (period.compareTo(modernPeriod) < 0) {
                return 0;
            }
        }
        return amount;
    }
}
