/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-02-01 21:38 创建
 */
package org.antframework.idcenter.facade.result;

import org.antframework.common.util.facade.AbstractResult;
import org.antframework.idcenter.facade.vo.IderInfo;

/**
 * 查找id提供者result
 */
public class FindIderResult extends AbstractResult {
    // id提供者info
    private IderInfo iderInfo;

    public IderInfo getIderInfo() {
        return iderInfo;
    }

    public void setIderInfo(IderInfo iderInfo) {
        this.iderInfo = iderInfo;
    }
}
