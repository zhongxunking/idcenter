/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-02-01 21:38 创建
 */
package org.antframework.idcenter.facade.result;

import lombok.Getter;
import lombok.Setter;
import org.antframework.common.util.facade.AbstractResult;
import org.antframework.idcenter.facade.info.IderInfo;

/**
 * 查找id提供者result
 */
@Getter
@Setter
public class FindIderResult extends AbstractResult {
    // id提供者（null表示不存在该id提供者）
    private IderInfo ider;
}
