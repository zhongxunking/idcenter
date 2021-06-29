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
import lombok.extern.slf4j.Slf4j;
import org.antframework.common.util.facade.BizException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.FacadeUtils;
import org.antframework.common.util.facade.Status;
import org.antframework.idcenter.biz.util.Iders;
import org.antframework.idcenter.facade.info.IderInfo;
import org.antframework.idcenter.facade.result.AcquireIdsResult;
import org.antframework.idcenter.facade.vo.IdSegment;
import org.antframework.idcenter.web.WebConfiguration;
import org.antframework.idcenter.web.id.Ider;
import org.antframework.idcenter.web.id.IderContext;
import org.apache.commons.lang3.StringUtils;
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
@Slf4j
public class IderController {
    // 单次获取id的最大数量缓存
    private final LoadingCache<String, Integer> maxAmountCache = Caffeine.newBuilder()
            .refreshAfterWrite(Duration.ofMinutes(1))
            .build(this::findMaxAmount);
    // 配置
    private final WebConfiguration.IdcenterProperties properties;
    // id提供者上下文
    private final IderContext iderContext;

    /**
     * 获取批量id
     *
     * @param iderId id提供者的id（id编码）
     * @param amount id数量
     * @return 获取批量id-result
     */
    @RequestMapping("/acquireIds")
    public AcquireIdsResult acquireIds(String iderId, Integer amount) {
        log.info("收到请求-获取批量id：iderId={},amount={}", iderId, amount);
        if (StringUtils.isEmpty(iderId)) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), "iderId不能为空");
        }
        if (amount == null || amount <= 0) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), "amount不能为空且必须大于0");
        }
        // 计算id数量
        int realAmount = computeRealAmount(iderId, amount);
        // 获取批量id
        List<IdSegment> idSegments = doAcquireIds(iderId, realAmount);
        // 构造result
        AcquireIdsResult result = FacadeUtils.buildSuccess(AcquireIdsResult.class);
        result.setIdSegments(idSegments);
        log.info("执行结果-获取批量id：result={}", result);
        return result;
    }

    // 计算真实的数量
    private int computeRealAmount(String iderId, int amount) {
        // 计算id数量
        Integer maxAmount = maxAmountCache.get(iderId);
        if (maxAmount == null) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", iderId));
        }
        int realAmount = amount;
        if (maxAmount >= 0 && realAmount > maxAmount) {
            realAmount = maxAmount;
            log.warn("期望获取id的数量[{}]过多，调整到[{}]", amount, realAmount);
        }
        return realAmount;
    }

    // 获取批量id
    private List<IdSegment> doAcquireIds(String iderId, int amount) {
        Ider ider = iderContext.getIder(iderId);
        return ider.acquireIds(amount);
    }

    // 查找单次获取id的最大数量
    private Integer findMaxAmount(String iderId) {
        Integer maxAmount = null;
        IderInfo ider = Iders.findIder(iderId);
        if (ider != null) {
            maxAmount = ider.getMaxAmount();
            if (maxAmount == null) {
                maxAmount = -1;
            }
        }
        return maxAmount;
    }
}
