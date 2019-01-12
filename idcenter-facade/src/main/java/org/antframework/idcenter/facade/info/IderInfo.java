/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-27 20:11 创建
 */
package org.antframework.idcenter.facade.info;

import lombok.Getter;
import lombok.Setter;
import org.antframework.common.util.facade.AbstractInfo;
import org.antframework.common.util.id.PeriodType;

import java.util.List;

/**
 * id提供者info
 */
@Getter
@Setter
public class IderInfo extends AbstractInfo {
    // id提供者的id（id编码）
    private String iderId;
    // id提供者的名称
    private String iderName;
    // 周期类型
    private PeriodType periodType;
    // 一个周期内id最大值（不包含），null表示不限制
    private Long maxId;
    // 单次获取id的最大数量（包含），null表示不限制
    private Integer maxAmount;
    // 因数（生产者数量）
    private Integer factor;
    // 生产者
    private List<IdProducerInfo> idProducers;
}
