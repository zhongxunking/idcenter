/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-20 23:08 创建
 */
package org.antframework.idcenter.web.id;

import org.antframework.common.util.other.Cache;
import org.antframework.idcenter.web.id.core.DefaultIder;

import java.util.function.Function;

/**
 * id提供者上下文
 */
public class IderContext {
    // id提供者缓存
    private final Cache<String, Ider> iderCache = new Cache<>(new Function<String, Ider>() {
        @Override
        public Ider apply(String key) {
            return new DefaultIder(key, minDuration, maxDuration, maxBlockedThreads);
        }
    });
    // 最小预留时间（毫秒）
    private final long minDuration;
    // 最大预留时间（毫秒）
    private final long maxDuration;
    // 最多被阻塞的线程数量
    private final Integer maxBlockedThreads;

    /**
     * 构造id提供者上下文
     *
     * @param minDuration       最小预留时间（毫秒）
     * @param maxDuration       最大预留时间（毫秒）
     * @param maxBlockedThreads 最多被阻塞的线程数量（null表示不限制数量）
     */
    public IderContext(long minDuration, long maxDuration, Integer maxBlockedThreads) {
        if (minDuration < 0 || maxDuration < minDuration || (maxBlockedThreads != null && maxBlockedThreads < 0)) {
            throw new IllegalArgumentException(String.format("初始化IdersContext的参数不合法：minDuration=%d,maxDuration=%d,maxBlockedThreads=%s", minDuration, maxDuration, maxBlockedThreads));
        }
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.maxBlockedThreads = maxBlockedThreads;
    }

    /**
     * 获取id提供者
     *
     * @param iderId id提供者的id（id编码）
     * @return id提供者
     */
    public Ider getIder(String iderId) {
        return iderCache.get(iderId);
    }
}
