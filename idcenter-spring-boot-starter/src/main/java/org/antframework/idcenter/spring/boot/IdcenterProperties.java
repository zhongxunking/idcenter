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
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

/**
 * idcenter配置
 */
@ConfigurationProperties("idcenter")
@Validated
@Getter
@Setter
public class IdcenterProperties {
    /**
     * 必填：idcenter服务端地址（比如：http://localhost:6210）
     */
    @NotBlank
    private String serverUrl;
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
     * 选填：最多被阻塞的线程数量（null表示不限制数量。默认为不限制数量）
     */
    @Min(0)
    private Integer maxBlockedThreads = null;
    /**
     * 选填：请求服务端的线程数量（默认为5）
     */
    @Min(1)
    private int requestServerThreads = 5;
}
