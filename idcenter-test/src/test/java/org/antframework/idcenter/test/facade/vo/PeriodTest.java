/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-22 23:39 创建
 */
package org.antframework.idcenter.test.facade.vo;

import org.antframework.idcenter.facade.enums.PeriodType;
import org.antframework.idcenter.facade.vo.Period;
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
public class PeriodTest {
    private Date testDate = DateUtils.parseDate("2017-11-22 23:48:21.250", "yyyy-MM-dd HH:mm:ss.SSS");

    public PeriodTest() throws ParseException {
    }

    @Test
    public void testParsePeriod() throws ParseException {
        Date hourDate = DateUtils.parseDate("2017-11-22 23:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
        Assert.assertEquals(hourDate, new Period(PeriodType.HOUR, testDate).getDate());

        Date dayDate = DateUtils.parseDate("2017-11-22 00:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
        Assert.assertEquals(dayDate, new Period(PeriodType.DAY, testDate).getDate());

        Date monthDate = DateUtils.parseDate("2017-11-01 00:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
        Assert.assertEquals(monthDate, new Period(PeriodType.MONTH, testDate).getDate());

        Date yearDate = DateUtils.parseDate("2017-01-01 00:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
        Assert.assertEquals(yearDate, new Period(PeriodType.YEAR, testDate).getDate());

        Assert.assertEquals(null, new Period(PeriodType.NONE, testDate).getDate());
    }
}
