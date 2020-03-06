/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-31 22:23 创建
 */
package org.antframework.idcenter.client.core;

import org.antframework.common.util.id.Id;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * id仓库
 */
public class IdStorage {
    // id块队列
    private final Queue<IdChunk> idChunks = new ConcurrentLinkedQueue<>();
    // 仓库内id数量
    private final AtomicLong amount = new AtomicLong(0);

    /**
     * 获取id
     *
     * @return id（null表示无存量id）
     */
    public synchronized Id getId() {
        Id id;
        do {
            IdChunk idChunk = idChunks.peek();
            if (idChunk == null) {
                return null;
            }
            id = idChunk.getId();
            if (id == null) {
                idChunks.poll();
            }
        } while (id == null);
        amount.addAndGet(-1);

        return id;
    }

    /**
     * 添加id块
     */
    public synchronized void addIdChunk(IdChunk idChunk) {
        idChunks.offer(idChunk);
        amount.addAndGet(idChunk.getAmount(null));
    }

    /**
     * 清理过期的id块
     *
     * @param currentTime 当前时间（null表示不管是否过期）
     */
    public synchronized void clearOverdueIdChunks(Long currentTime) {
        IdChunk idChunk = idChunks.peek();
        while (idChunk != null && idChunk.getAmount(currentTime) <= 0) {
            idChunks.poll();
            amount.addAndGet(-idChunk.getAmount(null));
            idChunk = idChunks.peek();
        }
    }

    /**
     * 获取id数量
     *
     * @param currentTime 当前时间（null表示不管是否过期）
     * @return id数量
     */
    public long getAmount(Long currentTime) {
        if (currentTime == null) {
            return this.amount.get();
        }
        long amount = 0;
        for (IdChunk idChunk : idChunks) {
            amount += idChunk.getAmount(currentTime);
        }
        return amount;
    }
}
