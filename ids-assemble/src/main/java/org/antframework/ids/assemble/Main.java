/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-06-22 11:34 创建
 */
package org.antframework.ids.assemble;

import org.antframework.boot.core.AntBootApplication;
import org.antframework.boot.core.Apps;
import org.springframework.boot.SpringApplication;

/**
 * 程序启动入口
 */
@AntBootApplication(appCode = "ids")
public class Main {
    public static void main(String[] args) {
        Apps.setProfileIfNotExists("dev");
        SpringApplication.run(Main.class, args);
    }
}
