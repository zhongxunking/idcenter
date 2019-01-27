/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-27 21:16 创建
 */
package org.antframework.idcenter.spring.boot;

import lombok.Getter;
import lombok.Setter;
import org.antframework.idcenter.spring.context.Contexts;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.Min;

/**
 * id中心配属性
 */
@ConfigurationProperties("idcenter")
@Getter
@Setter
public class IdcenterProperties {
    /**
     * 实例
     */
    public static final IdcenterProperties INSTANCE = Contexts.buildProperties(IdcenterProperties.class);

    /**
     * 必填：id中心服务端地址（比如：http://localhost:6210）
     */
    @NotBlank
    private String serverUrl;
    /**
     * 选填：最小预留时间（单位：毫秒。默认为10分钟）
     */
    @Min(1)
    private long minDuration = 10 * 60 * 1000;
    /**
     * 选填：最大预留时间（单位：毫秒。默认为15分钟）
     */
    @Min(1)
    private long maxDuration = 15 * 60 * 1000;
}
