/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-02 21:51 创建
 */
package org.antframework.idcenter.client.core;

import org.antframework.idcenter.client.IdAcquirer;

/**
 * 可配置的id获取器
 */
public interface ConfigurableIdAcquirer extends IdAcquirer {

    /**
     * 关闭
     */
    void close();
}
