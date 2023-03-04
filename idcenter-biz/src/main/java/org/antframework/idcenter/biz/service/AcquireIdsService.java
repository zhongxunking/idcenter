/*
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-02 20:48 创建
 */
package org.antframework.idcenter.biz.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antframework.common.util.facade.BizException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.Status;
import org.antframework.common.util.id.Period;
import org.antframework.idcenter.dal.dao.IderDao;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.facade.order.AcquireIdsOrder;
import org.antframework.idcenter.facade.result.AcquireIdsResult;
import org.antframework.idcenter.facade.vo.IdSegment;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 获取批量id服务
 */
@Service(enableTx = true)
@AllArgsConstructor
@Slf4j
public class AcquireIdsService {
    // id提供者dao
    private final IderDao iderDao;

    @ServiceExecute
    public void execute(ServiceContext<AcquireIdsOrder, AcquireIdsResult> context) {
        AcquireIdsOrder order = context.getOrder();
        AcquireIdsResult result = context.getResult();

        Ider ider = iderDao.findLockByIderId(order.getIderId());
        if (ider == null) {
            throw new BizException(Status.FAIL, CommonResultCode.INVALID_PARAMETER.getCode(), String.format("id提供者[%s]不存在", order.getIderId()));
        }
        log.info("生产id前的ider:{}", ider);
        // 现代化ider
        modernizeIder(ider);
        // 生产id
        List<IdSegment> idSegments = produceIds(ider, order.getAmount());
        result.setIdSegments(idSegments);

        iderDao.save(ider);
        log.info("生产id后的ider:{}", ider);
    }

    // 现代化ider
    private void modernizeIder(Ider ider) {
        Period period = computePeriod(ider);
        Period modernPeriod = new Period(ider.getPeriodType(), new Date());
        if (period.compareTo(modernPeriod) < 0) {
            ider.setCurrentPeriod(modernPeriod.getDate());
            ider.setCurrentId(0L);
        }
    }

    // 生产id
    private List<IdSegment> produceIds(Ider ider, int amount) {
        List<IdSegment> idSegments = new ArrayList<>();

        long newCurrentId = ider.getCurrentId() + amount;
        Assert.isTrue(newCurrentId >= ider.getCurrentId(), "运算中超过long类型最大值，无法进行计算");
        long anchorId = ider.getCurrentId();
        while (anchorId < newCurrentId) {
            int idAmount = computeIdAmountOfPeriod(anchorId, newCurrentId, ider.getMaxId());
            idSegments.add(buildIdSegment(ider, anchorId, idAmount));
            anchorId += idAmount;
        }
        updateIder(ider, newCurrentId);

        return idSegments;
    }

    // 计算anchorId所在周期内生产的id数量
    private int computeIdAmountOfPeriod(long anchorId, long newCurrentId, Long maxId) {
        long anchorEndId = newCurrentId;
        if (maxId != null) {
            long anchorMaxId = anchorId / maxId * maxId + maxId;
            Assert.isTrue(anchorMaxId >= anchorId, "运算中超过long类型最大值，无法进行计算");
            anchorEndId = Math.min(anchorEndId, anchorMaxId);
        }
        return (int) (anchorEndId - anchorId);
    }

    // 构建id段
    private IdSegment buildIdSegment(Ider ider, long anchorId, int idAmount) {
        Period period = computePeriod(ider);
        long startId = anchorId;
        if (ider.getMaxId() != null) {
            period = period.grow((int) (startId / ider.getMaxId()));
            startId = startId % ider.getMaxId();
        }
        return new IdSegment(period, startId, idAmount);
    }

    // 更新ider
    private void updateIder(Ider ider, long newCurrentId) {
        Period period = computePeriod(ider);
        long id = newCurrentId;
        if (ider.getMaxId() != null) {
            period = period.grow((int) (id / ider.getMaxId()));
            id = id % ider.getMaxId();
        }
        ider.setCurrentPeriod(period.getDate());
        ider.setCurrentId(id);
    }

    // 计算ider的当前周期
    private Period computePeriod(Ider ider) {
        return new Period(ider.getPeriodType(), ider.getCurrentPeriod());
    }
}
