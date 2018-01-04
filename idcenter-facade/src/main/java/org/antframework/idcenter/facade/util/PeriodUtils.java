/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-21 21:38 创建
 */
package org.antframework.idcenter.facade.util;

import org.antframework.idcenter.facade.enums.PeriodType;
import org.apache.commons.lang3.time.DateUtils;

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
    public static Date parse(PeriodType periodType, Date date) {
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

    /**
     * 周期增加
     *
     * @param periodType 周期类型
     * @param period     周期
     * @param length     增加长度
     * @return 增加后的周期
     */
    public static Date grow(PeriodType periodType, Date period, int length) {
        switch (periodType) {
            case HOUR:
                return DateUtils.addHours(period, length);
            case DAY:
                return DateUtils.addDays(period, length);
            case MONTH:
                return DateUtils.addMonths(period, length);
            case YEAR:
                return DateUtils.addYears(period, length);
            case NONE:
                if (length == 0) {
                    return null;
                }
                throw new IllegalArgumentException("周期类型为NONE的周期不能进行增加");
            default:
                throw new IllegalArgumentException("无法识别的周期类型：" + periodType);
        }
    }

    /**
     * 比较周期大小
     *
     * @param period1 需进行比较的周期1
     * @param period2 需进行比较的周期2
     * @return 比较结果
     */
    public static int compare(Date period1, Date period2) {
        if (period1 == period2) {
            return 0;
        }
        if (period1.getTime() > period2.getTime()) {
            return 1;
        } else if (period1.getTime() < period2.getTime()) {
            return -1;
        }
        return 0;
    }
}
