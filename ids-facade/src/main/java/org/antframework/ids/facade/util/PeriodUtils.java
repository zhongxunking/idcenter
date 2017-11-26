/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-21 21:38 创建
 */
package org.antframework.ids.facade.util;

import org.antframework.ids.facade.enums.PeriodType;

import java.util.Calendar;
import java.util.Date;

/**
 * 周期工具类
 */
public class PeriodUtils {

    /**
     * 解析出周期
     *
     * @param periodType 周期类型
     * @param date       时间
     * @return 周期（null 如果周期类型为PeriodType.NONE）
     */
    public static Date parsePeriod(PeriodType periodType, Date date) {
        if (periodType == PeriodType.NONE) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false);
        calendar.setTime(date);

        switch (periodType) {
            case YEAR:
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
            case MONTH:
                calendar.set(Calendar.DAY_OF_MONTH, 1);
            case DAY:
                calendar.set(Calendar.HOUR_OF_DAY, 0);
            case HOUR:
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                break;
            default:
                throw new IllegalArgumentException("无法识别的周期类型：" + periodType);
        }
        return calendar.getTime();
    }
}
