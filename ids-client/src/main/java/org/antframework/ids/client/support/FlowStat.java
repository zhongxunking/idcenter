/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-02 20:31 创建
 */
package org.antframework.ids.client.support;

import org.antframework.ids.client.IdContext;

/**
 * 流量统计
 */
public class FlowStat {
    // 最大时间（毫秒）
    private final long maxTime;
    // 最小时间（毫秒）
    private final long minTime;
    // 统计开始时间
    private long startTime;
    // id使用量统计
    private long count;
    // 下一个统计开始时间
    private long nextStartTime;
    // 下一个id使用量统计
    private long nextCount;

    public FlowStat(IdContext.InitParams initParams) {
        maxTime = initParams.getMaxTime();
        minTime = initParams.getMinTime();
        startTime = System.currentTimeMillis();
        count = 0;
        nextStartTime = System.currentTimeMillis();
        nextCount = 0;
    }

    /**
     * 增加统计
     */
    public void addCount() {
        count++;
        nextCount++;
    }

    /**
     * 切换到下一个统计
     */
    public void next() {
        startTime = nextStartTime;
        count = nextCount;
        nextStartTime = System.currentTimeMillis();
        nextCount = 0;
    }

    /**
     * 计算缺口
     *
     * @param remainAmount 剩余数量
     * @return 缺口
     */
    public int calcGap(int remainAmount) {
        long statDuration = System.currentTimeMillis() - startTime;
        int min = (int) (((double) minTime) / statDuration * count);
        if (remainAmount >= min) {
            return 0;
        }
        int max = (int) (((double) maxTime) / statDuration * count);
        return max - remainAmount;
    }
}
