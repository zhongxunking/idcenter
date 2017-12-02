/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-22 23:39 创建
 */
package org.antframework.ids.test.facade.util;

import org.antframework.ids.facade.enums.PeriodType;
import org.antframework.ids.facade.util.PeriodUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

/**
 * 周期工具类单元测试
 */
@Ignore
public class PeriodUtilsTest {
    private Date testDate = DateUtils.parseDate("2017-11-22 23:48:21.250", "yyyy-MM-dd HH:mm:ss.SSS");

    public PeriodUtilsTest() throws ParseException {
    }

    @Test
    public void testParsePeriod() throws ParseException {
        Date hourDate = DateUtils.parseDate("2017-11-22 23:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
        Assert.assertEquals(hourDate, PeriodUtils.parse(PeriodType.HOUR, testDate));

        Date dayDate = DateUtils.parseDate("2017-11-22 00:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
        Assert.assertEquals(dayDate, PeriodUtils.parse(PeriodType.DAY, testDate));

        Date monthDate = DateUtils.parseDate("2017-11-01 00:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
        Assert.assertEquals(monthDate, PeriodUtils.parse(PeriodType.MONTH, testDate));

        Date yearDate = DateUtils.parseDate("2017-01-01 00:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
        Assert.assertEquals(yearDate, PeriodUtils.parse(PeriodType.YEAR, testDate));

        Assert.assertEquals(null, PeriodUtils.parse(PeriodType.NONE, testDate));
    }
}
