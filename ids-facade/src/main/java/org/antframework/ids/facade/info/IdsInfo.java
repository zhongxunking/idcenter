/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-29 22:48 创建
 */
package org.antframework.ids.facade.info;

import org.antframework.ids.facade.enums.PeriodType;

import java.util.Date;

/**
 * 批量id信息
 */
public class IdsInfo extends AbstractInfo {
    // id编码
    private String idCode;
    // 周期类型
    private PeriodType periodType;
    // 周期
    private Date period;
    // 开始id
    private long startId;
    //
    private int factor;

    private int amount;
}
