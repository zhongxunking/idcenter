/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-09 16:51 创建
 */
package org.antframework.ids.client;

/**
 * id上下文
 */
public class IdContext {
    // 初始化参数
    private InitParams initParams;

    public IdContext(InitParams initParams) {
        this.initParams = initParams;
    }

    /**
     * 客户端初始化参数
     */
    public static class InitParams {
        // 必填：id编码
        private String idCode;
        // 必填：服务端地址
        private String serverUrl;
        // 最大时间（毫秒）
        private long maxTime;
        // 最小时间（毫秒）
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
