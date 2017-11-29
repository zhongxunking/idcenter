/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-27 00:34 创建
 */
package org.antframework.ids.biz.util;

import org.antframework.ids.dal.entity.Ider;
import org.antframework.ids.dal.entity.Producer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Comparator;
import java.util.Date;

/**
 * id生产者工具类
 */
public class ProducerUtils {

    /**
     * 生产id
     *
     * @param producer id生产者
     * @param ider     id提供者
     * @param amount   需生产的id个数
     */
    public static void produce(Producer producer, Ider ider, int amount) {
        long newCurrentId = producer.getCurrentId() + amount * ider.getFactor();
        if (ider.getMaxId() != null && newCurrentId >= ider.getMaxId()) {
            Date newCurrentPeriod = producer.getCurrentPeriod();
            int carry = (int) (newCurrentId / ider.getMaxId());
            switch (ider.getPeriodType()) {
                case HOUR:
                    newCurrentPeriod = DateUtils.addHours(newCurrentPeriod, carry);
                    break;
                case DAY:
                    newCurrentPeriod = DateUtils.addDays(newCurrentPeriod, carry);
                    break;
                case MONTH:
                    newCurrentPeriod = DateUtils.addMonths(newCurrentPeriod, carry);
                    break;
                case YEAR:
                    newCurrentPeriod = DateUtils.addYears(newCurrentPeriod, carry);
                    break;
                default:
                    throw new IllegalStateException(String.format("生产者[%s]剩余空间不足", producer.toString()));
            }
            producer.setCurrentPeriod(newCurrentPeriod);

            newCurrentId %= ider.getMaxId();
        }
        producer.setCurrentId(newCurrentId);
    }

    /**
     * id生产者比较器
     */
    public static class ProducerComparator implements Comparator<Producer> {

        @Override
        public int compare(Producer o1, Producer o2) {
            if (!StringUtils.equals(o1.getIdCode(), o2.getIdCode())) {
                throw new IllegalArgumentException(String.format("待比较的id生产者%s和%s不属于同一个id提供者，不能进行比较", o1.toString(), o2.toString()));
            }
            if (o1.getCurrentPeriod() != o2.getCurrentPeriod()) {
                if (o1.getCurrentPeriod().getTime() < o2.getCurrentPeriod().getTime()) {
                    return -1;
                } else if (o1.getCurrentPeriod().getTime() > o2.getCurrentPeriod().getTime()) {
                    return 1;
                }
            }
            if (o1.getCurrentId() < o2.getCurrentId()) {
                return -1;
            } else if (o1.getCurrentId() > o2.getCurrentId()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
