/*
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-06-22 13:06 创建
 */
package org.antframework.idcenter.web;

import lombok.Getter;
import lombok.Setter;
import org.antframework.idcenter.web.common.ManagerIders;
import org.antframework.idcenter.web.id.IderContext;
import org.antframework.manager.facade.event.ManagerDeletingEvent;
import org.antframework.manager.facade.info.ManagerInfo;
import org.bekit.event.annotation.DomainListener;
import org.bekit.event.annotation.Listen;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

/**
 * web层配置
 */
@Configuration
@EnableConfigurationProperties(WebConfiguration.IdcenterProperties.class)
@Import(WebConfiguration.ManagerListener.class)
public class WebConfiguration {
    // id提供者上下文
    @Bean
    public IderContext iderContext(IdcenterProperties properties) {
        return new IderContext(
                properties.getMinReserve(),
                properties.getMaxReserve(),
                properties.getMaxBlockedThreads(),
                properties.getRequestServiceThreads());
    }

    /**
     * 管理员监听器
     */
    @DomainListener
    public static class ManagerListener {
        // 监听管理员删除事件
        @Listen
        public void listenManagerDeletingEvent(ManagerDeletingEvent event) {
            ManagerInfo manager = event.getManager();
            ManagerIders.deletesByManager(manager.getManagerId());
        }
    }

    /**
     * idcenter配置
     */
    @ConfigurationProperties("idcenter")
    @Validated
    @Getter
    @Setter
    public static class IdcenterProperties {
        /**
         * 选填：最短时长储备量（单位：毫秒。默认为10分钟）
         */
        @Min(1)
        private long minReserve = 10 * 60 * 1000;
        /**
         * 选填：最长时长储备量（单位：毫秒。默认为15分钟）
         */
        @Min(1)
        private long maxReserve = 15 * 60 * 1000;
        /**
         * 选填：最多被阻塞的线程数量（null表示不限制数量。默认为100）
         */
        @Min(0)
        private Integer maxBlockedThreads = 100;
        /**
         * 选填：请求服务的线程数量（默认为20）
         */
        @Min(1)
        private int requestServiceThreads = 20;
    }
}
