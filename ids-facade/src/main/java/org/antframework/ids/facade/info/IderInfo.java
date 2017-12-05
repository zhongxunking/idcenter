/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-27 20:11 创建
 */
package org.antframework.ids.facade.info;

import org.antframework.ids.facade.enums.PeriodType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * id提供者信息
 */
public class IderInfo extends AbstractInfo {
    // id编码
    private String idCode;
    // 周期类型
    private PeriodType periodType;
    // 一个周期内id最大值（不包含），null表示不限制
    private Long maxId;
    // 一次获取id的最大数量（包含），null表示不限制
    private Integer maxAmount;
    // 因数（生产者数量）
    private Integer factor;
    // 生产者信息
    private List<ProducerInfo> producerInfos = new ArrayList<>();

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

    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    public Integer getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Integer maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Integer getFactor() {
        return factor;
    }

    public void setFactor(Integer factor) {
        this.factor = factor;
    }

    public List<ProducerInfo> getProducerInfos() {
        return producerInfos;
    }

    public void addProducerInfo(ProducerInfo producerInfo) {
        producerInfos.add(producerInfo);
    }

    /**
     * 生产者信息
     */
    public static class ProducerInfo extends AbstractInfo {
        // id编码
        private String idCode;
        // 序号
        private Integer index;
        // 当前周期
        private Date currentPeriod;
        // 当前Id
        private Long currentId;

        public String getIdCode() {
            return idCode;
        }

        public void setIdCode(String idCode) {
            this.idCode = idCode;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public Date getCurrentPeriod() {
            return currentPeriod;
        }

        public void setCurrentPeriod(Date currentPeriod) {
            this.currentPeriod = currentPeriod;
        }

        public Long getCurrentId() {
            return currentId;
        }

        public void setCurrentId(Long currentId) {
            this.currentId = currentId;
        }
    }
}
