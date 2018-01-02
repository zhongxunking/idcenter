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
    // 初始化参数
    private IdContext.InitParams initParams;
    // 统计开始时间
    private long startTime;
    // id使用量统计
    private long count;
    // 下一个统计开始时间
    private long nextStartTime;
    // 下一个id使用量统计
    private long nextCount;

    public FlowStat(IdContext.InitParams initParams) {
        this.initParams = initParams;
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
     * 计算差量
     *
     * @param remainAmount 剩余数量
     * @return 差量
     */
    public int calcGap(int remainAmount) {
        long statDuration = System.currentTimeMillis() - startTime;
        // 如果时钟被回拨
        if (statDuration <= 0) {
            if (remainAmount > 0) {
                return 0;
            } else {
                return initParams.getInitAmount();
            }
        }

        int min = (int) (((double) initParams.getMinTime()) / statDuration * count);
        if (remainAmount > min) {
            return 0;
        }
        int max = (int) (((double) initParams.getMaxTime()) / statDuration * count);
        int gap = max - remainAmount;

        return gap > 0 ? gap : 1;
    }
}
