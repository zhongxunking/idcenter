/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-20 23:11 创建
 */
package org.antframework.idcenter.web.id;

import org.antframework.idcenter.facade.vo.IdSegment;

import java.util.List;

/**
 * id提供者
 */
public interface Ider {

    /**
     * 获取批量id
     *
     * @param amount 获取的id数量
     * @return 批量id
     */
    List<IdSegment> acquireIds(int amount);
}
