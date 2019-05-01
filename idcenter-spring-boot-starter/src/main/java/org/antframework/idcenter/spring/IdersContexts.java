/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-27 21:13 创建
 */
package org.antframework.idcenter.spring;

import org.antframework.boot.core.Contexts;
import org.antframework.idcenter.client.Ider;
import org.antframework.idcenter.client.IdersContext;
import org.antframework.idcenter.spring.boot.IdcenterProperties;

/**
 * id提供者上下文操作类
 */
public final class IdersContexts {
    // id提供者上下文
    private static final IdersContext IDERS_CONTEXT;

    static {
        IdcenterProperties properties = Contexts.buildProperties(IdcenterProperties.class);
        IDERS_CONTEXT = new IdersContext(properties.getServerUrl(), properties.getMinDuration(), properties.getMaxDuration());
    }

    /**
     * 获取id提供者上下文
     *
     * @return id提供者上下文
     */
    public static IdersContext getContext() {
        return IDERS_CONTEXT;
    }

    /**
     * 获取id提供者
     *
     * @param iderId id提供者的id（id编码）
     * @return id提供者
     */
    public static Ider getIder(String iderId) {
        return IDERS_CONTEXT.getIder(iderId);
    }
}
