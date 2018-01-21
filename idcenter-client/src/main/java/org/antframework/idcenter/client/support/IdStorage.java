/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-31 22:23 创建
 */
package org.antframework.idcenter.client.support;

import org.antframework.idcenter.client.Id;
import org.antframework.idcenter.client.IdContext;
import org.antframework.idcenter.client.core.Ids;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * id仓库
 */
public class IdStorage {
    // 批量id队列
    private final Queue<Ids> idsQueue = new ConcurrentLinkedQueue<>();
    // 仓库内id个数
    private AtomicLong amount = new AtomicLong(0);
    // 允许的周期误差
    private Long periodError;

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
                amount.addAndGet(-ids.getAmount(null));
            }
        } while (id == null);
        amount.addAndGet(-1);

        return id;
    }

    /**
     * 添加批量id
     */
    public void addIds(Ids ids) {
        idsQueue.offer(ids);
        amount.addAndGet(ids.getAmount(null));
    }

    /**
     * 获取id数量
     *
     * @param allowPeriodError 是否允许周期误差
     * @return id数量
     */
    public long getAmount(boolean allowPeriodError) {
        if (allowPeriodError) {
            return this.amount.get();
        }
        int amount = 0;
        for (Ids ids : idsQueue) {
            amount += ids.getAmount(0L);
        }
        return amount;
    }
}
