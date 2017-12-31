/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-29 22:45 创建
 */
package org.antframework.ids.facade.result;

import org.antframework.common.util.facade.AbstractResult;
import org.antframework.ids.facade.enums.PeriodType;
import org.antframework.ids.facade.info.IdsInfo;

import java.util.List;

/**
 * 获取批量id-result
 */
public class AcquireIdsResult extends AbstractResult {
    // id编码
    private String idCode;
    // 周期类型
    private PeriodType periodType;
    // 获取到的批量id
    private List<IdsInfo> idsInfos;

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public List<IdsInfo> getIdsInfos() {
        return idsInfos;
    }

    public void setIdsInfos(List<IdsInfo> idsInfos) {
        this.idsInfos = idsInfos;
    }
}
