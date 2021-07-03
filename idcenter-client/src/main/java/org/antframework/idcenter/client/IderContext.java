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
import org.antframework.idcenter.client.support.TaskExecutor;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.function.Function;

/**
 * id提供者上下文
 */
public class IderContext {
    // id提供者缓存
    private final Cache<String, Ider> iderCache = new Cache<>(new Function<String, Ider>() {
        @Override
        public Ider apply(String key) {
            return new DefaultIder(
                    key,
                    minReserve,
                    maxReserve,
                    maxBlockedThreads,
                    serverRequester,
                    taskExecutor);
        }
    });
    // 最短时长储备量（毫秒）
    private final long minReserve;
    // 最长时长储备量（毫秒）
    private final long maxReserve;
    // 最多被阻塞的线程数量
    private final Integer maxBlockedThreads;
    // 服务端请求器
    private final ServerRequester serverRequester;
    // 任务执行器
    private final TaskExecutor taskExecutor;

    /**
     * 构造id提供者上下文
     *
     * @param serverUrl            服务端地址
     * @param minReserve           最短时长储备量（毫秒）
     * @param maxReserve           最长时长储备量（毫秒）
     * @param maxBlockedThreads    最多被阻塞的线程数量（null表示不限制数量）
     * @param requestServerThreads 请求服务端的线程数量
     */
    public IderContext(String serverUrl,
                       long minReserve,
                       long maxReserve,
                       Integer maxBlockedThreads,
                       int requestServerThreads) {
        if (StringUtils.isBlank(serverUrl)
                || minReserve < 0
                || maxReserve < minReserve
                || (maxBlockedThreads != null && maxBlockedThreads < 0)
                || requestServerThreads <= 0) {
            throw new IllegalArgumentException(String.format("初始化IderContext的参数不合法：serverUrl=%s,minReserve=%d,maxReserve=%d,maxBlockedThreads=%s,requestServerThreads=%d", serverUrl, minReserve, maxReserve, maxBlockedThreads, requestServerThreads));
        }
        this.minReserve = minReserve;
        this.maxReserve = maxReserve;
        this.maxBlockedThreads = maxBlockedThreads;
        this.serverRequester = new ServerRequester(serverUrl);
        this.taskExecutor = new TaskExecutor(requestServerThreads);
    }

    /**
     * 获取已获取id的id提供者的id（id编码）
     */
    public Set<String> getIderIds() {
        return iderCache.getAllKeys();
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
