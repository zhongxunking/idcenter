/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-02 20:31 创建
 */
package org.antframework.idcenter.client.support;

import org.antframework.idcenter.client.IdContext;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流量统计
 */
public class FlowStat {
    private static final Random RANDOM = new Random();
    // 初始化参数
    private IdContext.InitParams initParams;
    // 统计开始时间
    private long startTime;
    // id使用量统计
    private AtomicInteger count;
    // 下一个统计开始时间
    private long nextStartTime;
    // 下一个id使用量统计
    private AtomicInteger nextCount;

    public FlowStat(IdContext.InitParams initParams) {
        this.initParams = initParams;
        startTime = nextStartTime = System.currentTimeMillis();
        count = new AtomicInteger(0);
        nextCount = new AtomicInteger(0);
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
     * @param remain 剩余数量
     * @return 差量
     */
    public int calcGap(long remain) {
        if (remain > Integer.MAX_VALUE) {
            // 如果id剩余数量超过int最大值，则表示id数量充足
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
            if (remain > 0) {
                return 0;
            } else {
                return initParams.getInitAmount();
            }
        }

        int min = (int) (((double) initParams.getMinTime()) / statDuration * count.get());
        if (remain > min) {
            return 0;
        }
        int max = (int) (((double) initParams.getMaxTime()) / statDuration * count.get());
        if (max < min) {
            // 运算中超出int最大值，无法进行计算
            return 0;
        }
        int gap = (int) (max - remain);
        if (min > 0) {
            // 进行浮动
            gap -= RANDOM.nextInt(max - min + 1) * (((double) (min - remain)) / min);
        }

        return gap > 0 ? gap : 1;
    }
}
