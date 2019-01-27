/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-08-22 11:40 创建
 */
package org.antframework.idcenter.spring.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * 上下文持有器
 */
public final class Contexts {
    // spring环境
    private static ConfigurableEnvironment environment;
    // spring容器
    private static ConfigurableApplicationContext applicationContext;

    /**
     * 设置spring环境
     */
    public static void setEnvironment(ConfigurableEnvironment environment) {
        Contexts.environment = environment;
    }

    /**
     * 设置spring容器
     */
    public static void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        Contexts.applicationContext = applicationContext;
    }

    /**
     * 获取Environment
     */
    public static Environment getEnvironment() {
        if (getApplicationContext() == null) {
            return environment;
        } else {
            return getApplicationContext().getEnvironment();
        }
    }

    /**
     * 获取ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 根据environment构建属性对象
     *
     * @param targetClass 目标类型
     * @return 属性对象
     */
    public static <T> T buildProperties(Class<T> targetClass) {
        PropertiesBinder binder = new PropertiesBinder(((ConfigurableEnvironment) getEnvironment()).getPropertySources());
        return binder.build(targetClass);
    }
}
