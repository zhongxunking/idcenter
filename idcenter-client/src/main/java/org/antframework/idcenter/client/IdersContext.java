/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-20 23:08 创建
 */
package org.antframework.idcenter.client;

import org.antframework.common.util.other.Cache;
import org.antframework.idcenter.client.core.DefaultIder;
import org.antframework.idcenter.client.support.ServerRequester;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

/**
 * id提供者上下文
 */
public class IdersContext {
    // id提供者缓存
    private final Cache<String, Ider> idersCache = new Cache<>(new Function<String, Ider>() {
        @Override
        public Ider apply(String key) {
            return new DefaultIder(key, minDuration, maxDuration, serverRequester);
        }
    });
    // 最小预留时间（毫秒）
    private final long minDuration;
    // 最大预留时间（毫秒）
    private final long maxDuration;
    // 服务端请求器
    private final ServerRequester serverRequester;

    /**
     * 构造id提供者上下文
     *
     * @param serverUrl   服务端地址
     * @param minDuration 最小预留时间（毫秒）
     * @param maxDuration 最大预留时间（毫秒）
     */
    public IdersContext(String serverUrl, long minDuration, long maxDuration) {
        if (StringUtils.isBlank(serverUrl) || minDuration < 0 || maxDuration < minDuration) {
            throw new IllegalArgumentException(String.format("初始化id中心客户端的参数不合法：serverUrl=%s,minDuration=%d,maxDuration=%d", serverUrl, minDuration, maxDuration));
        }
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        serverRequester = new ServerRequester(serverUrl);
    }

    /**
     * 获取id提供者
     *
     * @param iderId id提供者的id（id编码）
     * @return id提供者
     */
    public Ider getIder(String iderId) {
        return idersCache.get(iderId);
    }
}
