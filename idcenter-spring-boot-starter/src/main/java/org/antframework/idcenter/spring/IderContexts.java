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
import org.antframework.idcenter.client.IderContext;

/**
 * id提供者上下文操作类
 */
public final class IderContexts {
    // id提供者上下文
    private static final IderContext IDER_CONTEXT = Contexts.getApplicationContext().getBean(IderContext.class);

    /**
     * 获取id提供者上下文
     *
     * @return id提供者上下文
     */
    public static IderContext getContext() {
        return IDER_CONTEXT;
    }

    /**
     * 获取id提供者
     *
     * @param iderId id提供者的id（id编码）
     * @return id提供者
     */
    public static Ider getIder(String iderId) {
        return IDER_CONTEXT.getIder(iderId);
    }
}
