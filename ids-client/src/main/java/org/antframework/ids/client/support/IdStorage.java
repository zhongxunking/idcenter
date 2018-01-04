/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-31 22:23 创建
 */
package org.antframework.ids.client.support;

import org.antframework.ids.client.Id;
import org.antframework.ids.client.IdContext;
import org.antframework.ids.client.core.Ids;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * id仓库
 */
public class IdStorage {
    // 批量id队列
    private final Queue<Ids> idsQueue = new ConcurrentLinkedQueue<>();
    // 允许的周期误差
    private long periodError;

    public IdStorage(IdContext.InitParams initParams) {
        periodError = initParams.getMaxTime() - initParams.getMinTime();
    }

    /**
     * 获取id
     *
     * @return null 如果无存量id
     */
    public Id getId() {
        Id id;
        do {
            Ids ids = idsQueue.peek();
            if (ids == null) {
                return null;
            }
            id = ids.getId(periodError);
            if (id == null) {
                idsQueue.poll();
            }
        } while (id == null);

        return id;
    }

    /**
     * 添加批量id
     */
    public void addIds(Ids ids) {
        idsQueue.offer(ids);
    }

    /**
     * 获取id数量
     *
     * @param allowPeriodError 是否允许周期误差
     * @return id数量
     */
    public int getAmount(boolean allowPeriodError) {
        long periodError = allowPeriodError ? this.periodError : 0;

        int amount = 0;
        for (Ids ids : idsQueue) {
            amount += ids.getAmount(periodError);
        }
        return amount;
    }
}
