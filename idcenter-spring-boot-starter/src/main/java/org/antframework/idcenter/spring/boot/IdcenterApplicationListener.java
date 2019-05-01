/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-05-01 14:23 创建
 */
package org.antframework.idcenter.spring.boot;

import org.antframework.idcenter.spring.IdersContexts;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * idcenter初始化器
 */
public class IdcenterApplicationListener implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 触发初始化
        IdersContexts.getContext();
    }
}
