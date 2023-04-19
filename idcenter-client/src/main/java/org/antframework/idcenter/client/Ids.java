/*
 * 作者：钟勋 (email:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2023-04-19 16:33 创建
 */
package org.antframework.idcenter.client;

import org.antframework.common.util.id.Id;
import org.antframework.common.util.id.Period;
import org.antframework.common.util.id.PeriodType;
import org.antframework.common.util.kit.Exceptions;
import org.apache.http.util.Asserts;

import java.util.Calendar;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * id工具
 */
public class Ids {
    /**
     * 获取id（如果获的id为null或转换后为null，则会根据重试间隔时间和超时时间进行重试）
     *
     * @param ider          id提供者
     * @param idConverter   id转换器（转换后为null，则表示该id不符合，回重新获取id）
     * @param retryInterval 重试间隔时间（单位：毫秒）
     * @param timeout       超时时间（单位：毫秒）
     * @return id（不为null）
     * @throws TimeoutException     获取符合条件的id超时
     * @throws InterruptedException 等待获取id时被中断
     */
    public static Id acquire(Ider ider, Function<Id, Id> idConverter, long retryInterval, long timeout) {
        long deadline = System.currentTimeMillis() + timeout;
        Id id = acquireAndConvert(ider, idConverter);
        while (id == null) {
            if (System.currentTimeMillis() >= deadline) {
                Exceptions.rethrow(new TimeoutException(String.format("获取id超时[%dms]", timeout)));
            }
            Exceptions.call(() -> Thread.sleep(retryInterval));
            id = acquireAndConvert(ider, idConverter);
        }
        return id;
    }

    // 获取和转换ID
    private static Id acquireAndConvert(Ider ider, Function<Id, Id> idConverter) {
        Id id = ider.acquireId();
        if (id != null) {
            id = idConverter.apply(id);
        }
        return id;
    }

    /**
     * 设置新周期（十进制的id长度会增加两位）
     * <p>
     * 1.周期类型为HOUR时，新周期和原周期的差值不能超过11，即11个小时
     * 2.周期类型为DAY时，新周期和原周期的差值不能超过13，即13天
     * 3.周期类型为MONTH时，新周期和原周期的差值不能超过5，即5个月
     * 4.周期类型为YEAR时，不支持设置新周期
     * 5.周期类型为NONE时，不支持设置新周期
     *
     * @param id            id
     * @param newPeriod     新周期
     * @param decimalLength 原来的十进制长度
     * @return 新id
     */
    public static Id setNewPeriod(Id id, Period newPeriod, int decimalLength) {
        Integer idHead = computeIdHead(id, newPeriod);
        if (idHead == null) {
            return null;
        }

        long newId = idHead;
        for (int i = 0; i < decimalLength; i++) {
            long newerId = newId * 10;
            Asserts.check(newerId >= newId, "运算中超过long类型最大值，无法进行计算");
            newId = newerId;
        }
        long newerId = newId + id.getId();
        Asserts.check(newerId >= newId, "运算中超过long类型最大值，无法进行计算");
        newId = newerId;

        return new Id(newPeriod, newId);
    }

    // 计算新id头
    private static Integer computeIdHead(Id id, Period newPeriod) {
        if (id.getPeriod().getType() == PeriodType.NONE) {
            throw new IllegalArgumentException(String.format("周期类型为%s的id[%s]不支持重新分配新周期", id.getPeriod().getType(), id));
        }

        int idHead;

        int gap = newPeriod.subtract(id.getPeriod());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(id.getPeriod().getDate());
        switch (id.getPeriod().getType()) {
            case HOUR:
                if (gap > 11) {
                    return null;
                }
                idHead = calendar.get(Calendar.HOUR_OF_DAY);
                break;
            case DAY:
                if (gap > 13) {
                    return null;
                }
                idHead = calendar.get(Calendar.DAY_OF_MONTH) - 1;
                break;
            case MONTH:
                if (gap > 5) {
                    return null;
                }
                idHead = calendar.get(Calendar.MONTH);
                break;
            case YEAR:
            case NONE:
                throw new IllegalArgumentException(String.format("周期类型为%s的id[%s]不支持重新分配新周期", id.getPeriod().getType(), id));
            default:
                throw new IllegalArgumentException("无法识别的周期类型：" + id.getPeriod().getType());
        }

        return idHead;
    }

    /**
     * 格式化
     *
     * @param id            id
     * @param decimalLength 除周期外的十进制长度
     * @return 格式化后的id
     */
    public static String format(Id id, int decimalLength) {
        String period = id.getPeriod().toString();
        StringBuilder builder = new StringBuilder(period.length() + decimalLength);
        builder.append(period);
        String idStr = Long.toString(id.getId());
        for (int i = idStr.length(); i < decimalLength; i++) {
            builder.append('0');
        }
        builder.append(idStr);
        return builder.toString();
    }
}
