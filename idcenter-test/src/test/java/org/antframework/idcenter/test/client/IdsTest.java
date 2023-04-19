/*
 * 作者：钟勋 (email:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2023-04-19 17:32 创建
 */
package org.antframework.idcenter.test.client;

import org.antframework.common.util.id.Id;
import org.antframework.common.util.id.Period;
import org.antframework.common.util.id.PeriodType;
import org.antframework.idcenter.client.Ider;
import org.antframework.idcenter.client.Ids;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ids单元测试
 */
public class IdsTest {
    @Test
    public void testSetNewPeriod_hour() throws ParseException {
        Period period = new Period(PeriodType.HOUR, parseDate("2023-04-12 17:39:10.000"));
        Id id = new Id(period, 123);

        Period newPeriod = period.grow(5);
        Id newId = Ids.setNewPeriod(id, newPeriod, 7);
        Assert.assertEquals(newPeriod, newId.getPeriod());
        Assert.assertEquals(newId.getId(), 170000123);

        newPeriod = period.grow(12);
        newId = Ids.setNewPeriod(id, newPeriod, 7);
        Assert.assertNull(newId);
    }


    @Test
    public void testSetNewPeriod_day() throws ParseException {
        Period period = new Period(PeriodType.DAY, parseDate("2023-04-12 17:39:10.000"));
        Id id = new Id(period, 123);

        Period newPeriod = period.grow(5);
        Id newId = Ids.setNewPeriod(id, newPeriod, 7);
        Assert.assertEquals(newPeriod, newId.getPeriod());
        Assert.assertEquals(newId.getId(), 110000123);

        newPeriod = period.grow(14);
        newId = Ids.setNewPeriod(id, newPeriod, 7);
        Assert.assertNull(newId);
    }

    @Test
    public void testSetNewPeriod_month() throws ParseException {
        Period period = new Period(PeriodType.MONTH, parseDate("2023-04-12 17:39:10.000"));
        Id id = new Id(period, 123);

        Period newPeriod = period.grow(5);
        Id newId = Ids.setNewPeriod(id, newPeriod, 7);
        Assert.assertEquals(newPeriod, newId.getPeriod());
        Assert.assertEquals(newId.getId(), 30000123);

        newPeriod = period.grow(6);
        newId = Ids.setNewPeriod(id, newPeriod, 7);
        Assert.assertNull(newId);
    }

    @Test
    public void testSetNewPeriod_year() throws ParseException {
        Period period = new Period(PeriodType.YEAR, parseDate("2023-04-12 17:39:10.000"));
        Id id = new Id(period, 123);

        Period newPeriod = period.grow(5);
        try {
            Id newId = Ids.setNewPeriod(id, newPeriod, 7);
            throw new RuntimeException("单元测试未通过");
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
    }

    @Test
    public void testSetNewPeriod_none() throws ParseException {
        Period period = new Period(PeriodType.NONE, parseDate("2023-04-12 17:39:10.000"));
        Id id = new Id(period, 123);

        Period newPeriod = period.grow(0);
        try {
            Id newId = Ids.setNewPeriod(id, newPeriod, 7);
            throw new RuntimeException("单元测试未通过");
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
    }

    @Test
    public void testAcquire() throws ParseException {
        Period period = new Period(PeriodType.DAY, parseDate("2023-04-12 17:39:10.000"));
        Ider ider = new Ider() {
            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Id acquireId() {
                int count = counter.addAndGet(1);
                if (count <= 5) {
                    return null;
                } else if (count == 6) {
                    return new Id(period, 123);
                } else {
                    return new Id(period.grow(1), 123);
                }
            }
        };

        Period newPeriod = period.grow(14);

        Id acquiredId = Ids.acquire(ider, id -> {
            return Ids.setNewPeriod(id, newPeriod, 7);
        }, 100, 10000);

        Assert.assertEquals(newPeriod, acquiredId.getPeriod());
        Assert.assertEquals(acquiredId.getId(), 120000123);
    }

    @Test
    public void testFormat() throws ParseException {
        Period period = new Period(PeriodType.DAY, parseDate("2023-04-12 17:39:10.000"));
        Id id = new Id(period, 123);

        String formattedId = Ids.format(id, 10);
        Assert.assertEquals("202304120000000123", formattedId);
    }

    private static Date parseDate(String date) throws ParseException {
        return DateUtils.parseDate(date, "yyyy-MM-dd HH:mm:ss.SSS");
    }
}
