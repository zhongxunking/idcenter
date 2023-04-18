/*
 * 作者：钟勋 (email:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2023-04-18 17:16 创建
 */
package org.antframework.idcenter.client.util;

import org.antframework.common.util.id.Id;
import org.antframework.common.util.id.Period;

import java.util.Calendar;

/**
 * 周期指定器
 */
public class PeriodAssigner {
    /**
     * 指派新周期
     *
     * @param id            id
     * @param newPeriod     新周期
     * @param decimalLength 原来的十进制长度
     * @return 新id
     */
    public static Id assign(Id id, Period newPeriod, int decimalLength) {
        Integer idHead = computeIdHead(id, newPeriod);
        if (idHead == null) {
            return null;
        }

        long newId = idHead;
        for (int i = 0; i < decimalLength; i++) {
            newId *= 10;
        }
        newId += id.getId();
        return new Id(newPeriod, newId);
    }

    // 计算新id头
    private static Integer computeIdHead(Id id, Period newPeriod) {
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
}
