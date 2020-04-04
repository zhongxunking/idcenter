/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-20 23:11 创建
 */
package org.antframework.idcenter.client;

import org.antframework.common.util.id.Id;

/**
 * id提供者
 */
public interface Ider {

    /**
     * 获取id
     *
     * @return id（null表示无可用id）
     */
    Id acquireId();
}
