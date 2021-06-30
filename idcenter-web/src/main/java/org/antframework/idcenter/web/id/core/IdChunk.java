/*
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-15 23:46 创建
 */
package org.antframework.idcenter.web.id.core;

import lombok.AllArgsConstructor;
import org.antframework.common.util.id.Period;
import org.antframework.idcenter.facade.vo.IdSegment;

import java.util.Date;

/**
 * id块
 */
@AllArgsConstructor
public final class IdChunk {
    // 周期
    private final Period period;
    // 因数
    private final int factor;
    // 开始id（包含）
    private volatile long startId;
    // id个数
    private volatile int amount;

    /**
     * 获取批量id
     *
     * @param amount 获取的id数量
     * @return 批量id
     */
    public synchronized IdSegment getIds(int amount) {
        if (getAmount(null) <= 0) {
            return null;
        }
        int gotAmount = Math.min(amount, this.amount);
        IdSegment idSegment = new IdSegment(period, factor, startId, gotAmount);

        startId += factor * ((long) gotAmount);
        this.amount -= gotAmount;

        return idSegment;
    }

    /**
     * 获取id个数
     *
     * @param currentTime 当前时间（null表示不管是否过期）
     * @return id个数
     */
    public int getAmount(Long currentTime) {
        if (currentTime != null) {
            Period currentPeriod = new Period(period.getType(), new Date(currentTime));
            if (currentPeriod.compareTo(period) > 0) {
                return 0;
            }
        }
        return amount;
    }
}
