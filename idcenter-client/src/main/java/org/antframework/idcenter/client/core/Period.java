/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-06 16:06 创建
 */
package org.antframework.idcenter.client.core;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * 周期
 */
public class Period implements Comparable<Period>, Serializable {
    // 周期类型
    private final PeriodType type;
    // 周期时间
    private final Date date;

    /**
     * 创建周期
     *
     * @param type 周期类型
     * @param date 周期时间
     */
    public Period(PeriodType type, Date date) {
        if (type == null) {
            throw new NullPointerException("周期类型不能为null");
        }
        this.type = type;
        this.date = parseDate(type, date);
    }

    /**
     * 获取周期类型
     */
    public PeriodType getType() {
        return type;
    }

    /**
     * 获取周期时间
     */
    public Date getDate() {
        return date;
    }

    /**
     * 周期增加
     *
     * @param length 增加长度
     * @return 增加后的周期
     */
    public Period grow(int length) {
        Date date;
        switch (type) {
            case HOUR:
                date = DateUtils.addHours(this.date, length);
                break;
            case DAY:
                date = DateUtils.addDays(this.date, length);
                break;
            case MONTH:
                date = DateUtils.addMonths(this.date, length);
                break;
            case YEAR:
                date = DateUtils.addYears(this.date, length);
                break;
            case NONE:
                if (length != 0) {
                    throw new IllegalArgumentException("周期类型为NONE的周期不能进行增加");
                }
                date = null;
                break;
            default:
                throw new IllegalArgumentException("无法识别的周期类型：" + type);
        }

        return new Period(type, date);
    }

    @Override
    public int compareTo(Period other) {
        if (type != other.getType()) {
            throw new IllegalArgumentException("不同类型的周期不能进行比较");
        }
        if (date == other.getDate()) {
            return 0;
        }
        return date.compareTo(other.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, date);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Period)) {
            return false;
        }
        return compareTo((Period) obj) == 0;
    }

    @Override
    public String toString() {
        switch (type) {
            case HOUR:
                return DateFormatUtils.format(date, "yyyyMMddHH");
            case DAY:
                return DateFormatUtils.format(date, "yyyyMMdd");
            case MONTH:
                return DateFormatUtils.format(date, "yyyyMM");
            case YEAR:
                return DateFormatUtils.format(date, "yyyy");
            case NONE:
                return "";
            default:
                throw new IllegalArgumentException("无法识别的周期类型：" + type);
        }
    }

    // 解析出周期时间
    private static Date parseDate(PeriodType type, Date date) {
        if (type == PeriodType.NONE) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false);
        calendar.setTime(date);

        switch (type) {
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
                throw new IllegalArgumentException("无法识别的周期类型：" + type);
        }
        return calendar.getTime();
    }
}
