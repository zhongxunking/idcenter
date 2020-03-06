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
 * id块
 */
@AllArgsConstructor
public class IdChunk {
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
     * @return id（null表示无可用id）
     */
    public Id getId() {
        if (getAmount(null) <= 0) {
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
     * @param currentTime 当前时间（null表示不管是否过期）
     * @return id个数
     */
    public int getAmount(Date currentTime) {
        if (currentTime != null) {
            Period currentPeriod = new Period(period.getType(), currentTime);
            if (currentPeriod.compareTo(period) > 0) {
                return 0;
            }
        }
        return amount;
    }
}
