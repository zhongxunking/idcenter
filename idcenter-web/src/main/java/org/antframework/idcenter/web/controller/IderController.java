/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-17 22:48 创建
 */
package org.antframework.idcenter.web.controller;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.AllArgsConstructor;
import org.antframework.common.util.facade.BizException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.FacadeUtils;
import org.antframework.common.util.facade.Status;
import org.antframework.idcenter.biz.util.Iders;
import org.antframework.idcenter.facade.info.IderInfo;
import org.antframework.idcenter.facade.result.AcquireIdsResult;
import org.antframework.idcenter.facade.vo.IdSegment;
import org.antframework.idcenter.web.id.Ider;
import org.antframework.idcenter.web.id.IderContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

/**
 * id提供者controller
 */
@RestController
@RequestMapping("/ider")
@AllArgsConstructor
public class IderController {
    // 单次获取id的最大数量缓存
    private final LoadingCache<String, Integer> maxAmountCache = Caffeine.newBuilder()
            .refreshAfterWrite(Duration.ofMinutes(1))
            .build(this::findMaxAmount);
    // id提供者上下文
    private final IderContext iderContext;

    /**
     * 获取批量id
     *
     * @param iderId id提供者的id（id编码）
     * @param amount id数量
     * @return 获取结果
     */
    @RequestMapping("/acquireIds")
    public AcquireIdsResult acquireIds(String iderId, Integer amount) {
        // 计算id数量
        Integer maxAmount = maxAmountCache.get(iderId);
        if (maxAmount == null) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", iderId));
        }
        int realAmount = amount;
        if (maxAmount > 0) {
            realAmount = Math.min(realAmount, maxAmount);
        }
        // 获取批量id
        Ider ider = iderContext.getIder(iderId);
        List<IdSegment> idSegments = ider.acquireIds(realAmount);
        // 构造result
        AcquireIdsResult result = FacadeUtils.buildSuccess(AcquireIdsResult.class);
        result.setIdSegments(idSegments);
        return result;
    }

    // 查找单次获取id的最大数量
    private Integer findMaxAmount(String iderId) {
        Integer maxAmount = null;
        IderInfo ider = Iders.findIder(iderId);
        if (ider != null) {
            maxAmount = ider.getMaxAmount();
            if (maxAmount == null) {
                maxAmount = 0;
            }
        }
        return maxAmount;
    }
}
