/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-02 20:31 创建
 */
package org.antframework.idcenter.client.support;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 流量统计
 */
public class FlowStat {
    // 随机数
    private static final Random RANDOM = new Random();
    // 统计开始时间
    private volatile long startTime = System.currentTimeMillis();
    // id使用量统计
    private final AtomicLong count = new AtomicLong(0);
    // 下一个统计开始时间
    private volatile long nextStartTime = startTime;
    // 下一个id使用量统计
    private final AtomicLong nextCount = new AtomicLong(0);
    // 最小预留时间（毫秒）
    private final long minDuration;
    // 最大预留时间（毫秒）
    private final long maxDuration;

    /**
     * 构造流量统计
     *
     * @param minDuration 最小预留时间（毫秒）
     * @param maxDuration 最大预留时间（毫秒）
     */
    public FlowStat(long minDuration, long maxDuration) {
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
    }

    /**
     * 增加统计
     */
    public void addCount() {
        count.addAndGet(1);
        nextCount.addAndGet(1);
    }

    /**
     * 切换到下一个统计
     */
    public void next() {
        startTime = nextStartTime;
        count.set(nextCount.get());
        nextStartTime = System.currentTimeMillis();
        nextCount.set(0);
    }

    /**
     * 计算差量
     *
     * @param remain 余量
     * @return 差量
     */
    public int calcGap(long remain) {
        if (remain > Integer.MAX_VALUE) {
            // 如果余量超过int最大值，则表示余量充足
            return 0;
        }
        long statDuration = System.currentTimeMillis() - startTime;
        if (statDuration <= 0) {
            if (statDuration < 0) {
                // 如果时钟被回拨，则统计清零
                startTime = nextStartTime = System.currentTimeMillis();
                count.set(0);
                nextCount.set(0);
            }
            // 无法通过统计计算出差量
            return remain > 0 ? 0 : 1;
        }
        // 拷贝一份count的值（防止count被其他线程修改后导致计算出错）
        long count = this.count.get();
        long min = (long) (((double) minDuration) / statDuration * count);
        if (remain > min) {
            return 0;
        }
        long max = (long) (((double) maxDuration) / statDuration * count);
        if (max < min) {
            // 运算中超出long类型最大值，无法进行计算
            return 0;
        }
        long gap = max - remain;
        if (min > 0) {
            // 进行浮动（防止对服务端形成共振）
            int minMaxGap = (int) Math.min(max - min + 1, Integer.MAX_VALUE);
            gap -= RANDOM.nextInt(minMaxGap) * (((double) (min - remain)) / min);
        }
        // 差量不能超过int类型最大值
        gap = Math.min(gap, Integer.MAX_VALUE);
        return (int) Math.max(gap, 1);
    }
}
