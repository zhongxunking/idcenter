/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-09 16:51 创建
 */
package org.antframework.ids.client;

import org.antframework.ids.client.core.ConfigurableIdAcquirer;
import org.antframework.ids.client.core.DefaultIdAcquirer;

/**
 * id上下文
 */
public class IdContext {
    // 初始化参数
    private InitParams initParams;
    // id获取器
    private ConfigurableIdAcquirer idAcquirer;

    public IdContext(InitParams initParams) {
        this.initParams = initParams;
        idAcquirer = new DefaultIdAcquirer(initParams);
    }

    /**
     * 获取id获取器
     */
    public IdAcquirer getAcquirer() {
        return idAcquirer;
    }

    /**
     * 关闭
     */
    public void close() {
        idAcquirer.close();
    }

    /**
     * 客户端初始化参数
     */
    public static class InitParams {
        // 必填：id编码
        private String idCode;
        // 必填：服务端地址
        private String serverUrl;
        // 必填：初始化id数量
        private int initAmount;
        // 必填：最大时间（毫秒）
        private long maxTime;
        // 必填：最小时间（毫秒）
        private long minTime;

        public String getIdCode() {
            return idCode;
        }

        public void setIdCode(String idCode) {
            this.idCode = idCode;
        }

        public String getServerUrl() {
            return serverUrl;
        }

        public void setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
        }

        public int getInitAmount() {
            return initAmount;
        }

        public void setInitAmount(int initAmount) {
            this.initAmount = initAmount;
        }

        public long getMaxTime() {
            return maxTime;
        }

        public void setMaxTime(long maxTime) {
            this.maxTime = maxTime;
        }

        public long getMinTime() {
            return minTime;
        }

        public void setMinTime(long minTime) {
            this.minTime = minTime;
        }
    }
}
