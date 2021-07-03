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
import org.antframework.idcenter.web.id.support.TaskExecutor;

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
                    taskExecutor);
        }
    });
    // 最短时长储备量（毫秒）
    private final long minReserve;
    // 最长时长储备量（毫秒）
    private final long maxReserve;
    // 最多被阻塞的线程数量
    private final Integer maxBlockedThreads;
    // 任务执行器
    private final TaskExecutor taskExecutor;

    /**
     * 构造id提供者上下文
     *
     * @param minReserve            最短时长储备量（毫秒）
     * @param maxReserve            最长时长储备量（毫秒）
     * @param maxBlockedThreads     最多被阻塞的线程数量（null表示不限制数量）
     * @param requestServiceThreads 请求服务的线程数量
     */
    public IderContext(long minReserve,
                       long maxReserve,
                       Integer maxBlockedThreads,
                       int requestServiceThreads) {
        if (minReserve < 0
                || maxReserve < minReserve
                || (maxBlockedThreads != null && maxBlockedThreads < 0)
                || requestServiceThreads <= 0) {
            throw new IllegalArgumentException(String.format("初始化IderContext的参数不合法：minReserve=%d,maxReserve=%d,maxBlockedThreads=%s,requestServiceThreads=%d", minReserve, maxReserve, maxBlockedThreads, requestServiceThreads));
        }
        this.minReserve = minReserve;
        this.maxReserve = maxReserve;
        this.maxBlockedThreads = maxBlockedThreads;
        this.taskExecutor = new TaskExecutor(requestServiceThreads);
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
