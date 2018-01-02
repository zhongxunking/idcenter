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
import org.apache.commons.lang3.StringUtils;

/**
 * id上下文
 */
public class IdContext {
    // id获取器
    private ConfigurableIdAcquirer idAcquirer;

    public IdContext(InitParams initParams) {
        initParams.check();
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

        /**
         * 校验
         */
        public void check() {
            if (StringUtils.isBlank(idCode) || StringUtils.isBlank(serverUrl)
                    || initAmount <= 0 || maxTime <= 0 || minTime <= 0 || maxTime <= minTime) {
                throw new IllegalArgumentException("传入id中心客户端的初始化参数不合法");
            }
        }

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
