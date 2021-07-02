/*
 * 作者：钟勋 (email:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2021-05-09 18:53 创建
 */
package org.antframework.idcenter.spring.boot;

import lombok.AllArgsConstructor;
import org.antframework.idcenter.client.IderContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * idcenter自动配置
 */
@Configuration
@ConditionalOnMissingBean(IderContext.class)
@EnableConfigurationProperties(IdcenterProperties.class)
@AllArgsConstructor
public class IdcenterAutoConfiguration {
    // 配置
    private final IdcenterProperties properties;

    // id提供者上下文
    @ConditionalOnMissingBean
    @Bean(name = "org.antframework.idcenter.client.IderContext")
    public IderContext iderContext() {
        return new IderContext(
                properties.getServerUrl(),
                properties.getMinDuration(),
                properties.getMaxDuration(),
                properties.getMaxBlockedThreads(),
                properties.getRequestServerThreads());
    }
}
