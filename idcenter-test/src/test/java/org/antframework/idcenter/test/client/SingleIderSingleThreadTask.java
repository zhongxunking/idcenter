/* 
 * 作者：钟勋 (email:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2020-04-01 19:14 创建
 */
package org.antframework.idcenter.test.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antframework.common.util.id.Id;
import org.antframework.idcenter.client.Ider;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 单ider单线程任务
 */
@AllArgsConstructor
@Slf4j
public class SingleIderSingleThreadTask implements Runnable {
    // 任务序号
    private final int index;
    // id提供者
    private final Ider ider;
    // id数量
    private final int amountOfId;
    // 运行结果消费者
    private final Consumer<Performance> consumer;
    // 是否只测性能
    private final boolean onlyPerformance;

    @Override
    public void run() {
        Set<Id> idSet = onlyPerformance ? null : new HashSet<>(amountOfId);
        int amountOfNullId = 0;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < amountOfId; i++) {
            Id id = ider.acquireId();
            if (id != null) {
                if (!onlyPerformance) {
                    idSet.add(id);
                }
            } else {
                amountOfNullId++;
            }
        }
        long endTime = System.currentTimeMillis();

        double tps = (amountOfId - amountOfNullId) * 1000D / (endTime - startTime);
        Performance performance = new Performance(
                startTime,
                endTime,
                amountOfId,
                amountOfNullId,
                idSet,
                tps);
        log.info("单ider单线程任务{}：{}", index, performance);
        performance.check();
        consumer.accept(performance);
    }
}
