/*
 * 作者：钟勋 (email:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2020-04-01 19:15 创建
 */
package org.antframework.idcenter.test.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.antframework.common.util.id.Id;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Assert;

import java.util.Set;

/**
 * 性能
 */
@AllArgsConstructor
@Getter
public class Performance {
    // 开始时间
    private final long startTime;
    // 结束时间
    private final long endTime;
    // id数量
    private final int amountOfId;
    // id出现null次数
    private final int amountOfNullId;
    // id集合
    private final Set<Id> idSet;
    // tps
    private final double tps;

    public void check() {
        if (idSet != null) {
            Assert.assertEquals(amountOfId - amountOfNullId, idSet.size());
        }
    }

    @Override
    public String toString() {
        if (idSet == null) {
            return String.format("开始时间=%s，结束时间=%s，耗时=%d毫秒，获取id次数=%d，id出现null次数=%d，tps=%.2f",
                    DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm:ss.SSS"),
                    DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm:ss.SSS"),
                    endTime - startTime,
                    amountOfId,
                    amountOfNullId,
                    tps);
        } else {
            return String.format("开始时间=%s，结束时间=%s，耗时=%d毫秒，获取id次数=%d，id出现null次数=%d，获取到的id数量=%s，tps=%.2f",
                    DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm:ss.SSS"),
                    DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm:ss.SSS"),
                    endTime - startTime,
                    amountOfId,
                    amountOfNullId,
                    idSet.size(),
                    tps);
        }
    }
}
