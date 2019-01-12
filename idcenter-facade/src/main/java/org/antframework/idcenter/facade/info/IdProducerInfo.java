/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-12 23:05 创建
 */
package org.antframework.idcenter.facade.info;

import lombok.Getter;
import lombok.Setter;
import org.antframework.common.util.facade.AbstractInfo;

import java.util.Date;

/**
 * id生产者info
 */
@Getter
@Setter
public class IdProducerInfo extends AbstractInfo {
    // id提供者的id（id编码）
    private String iderId;
    // 序号
    private Integer index;
    // 当前周期
    private Date currentPeriod;
    // 当前Id（未使用）
    private Long currentId;
}
