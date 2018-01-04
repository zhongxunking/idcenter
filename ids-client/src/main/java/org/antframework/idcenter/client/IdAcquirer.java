/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-02 15:20 创建
 */
package org.antframework.idcenter.client;

/**
 * id获取器
 */
public interface IdAcquirer {

    /**
     * 获取id
     *
     * @return null 如果无可用的id
     */
    Id getId();
}
