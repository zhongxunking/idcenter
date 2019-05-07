/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-27 00:34 创建
 */
package org.antframework.idcenter.biz.util;

import org.antframework.common.util.facade.FacadeUtils;
import org.antframework.common.util.id.Period;
import org.antframework.idcenter.dal.entity.IdProducer;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.facade.vo.Ids;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * id生产者操作类
 */
public final class IdProducers {
    /**
     * 比较id生产者进度
     *
     * @param left  待比较的生产者
     * @param right 待比较的生产者
     * @return 比较结果
     */
    public static int compare(IdProducer left, IdProducer right) {
        if (!Objects.equals(left.getIderId(), right.getIderId())) {
            throw new IllegalArgumentException(String.format("待比较的id生产者%s和%s不属于同一个id提供者，不能进行比较", left.toString(), right.toString()));
        }
        // 比较周期
        int result = 0;
        if (left.getCurrentPeriod() != right.getCurrentPeriod()) {
            result = left.getCurrentPeriod().compareTo(right.getCurrentPeriod());
        }
        if (result == 0) {
            // 比较id
            result = left.getCurrentId().compareTo(right.getCurrentId());
        }
        return result;
    }

    /**
     * 生产id
     *
     * @param ider       id提供者
     * @param idProducer id生产者
     * @param amount     需生产的id个数
     * @return 生产出的id
     */
    public static List<Ids> produce(Ider ider, IdProducer idProducer, int amount) {
        return grow(ider, idProducer, ((long) amount) * ider.getFactor());
    }

    /**
     * 增加
     *
     * @param ider       id提供者
     * @param idProducer id生产者
     * @param length     增加长度
     * @return 增加过程中产生的id
     */
    public static List<Ids> grow(Ider ider, IdProducer idProducer, long length) {
        Assert.isTrue(length >= 0, "id生产者增加长度不能小于0");
        List<Ids> idses = new ArrayList<>();

        long newCurrentId = idProducer.getCurrentId() + length;
        Assert.isTrue(newCurrentId >= idProducer.getCurrentId(), "运算中超过long类型最大值，无法进行计算");
        Assert.isTrue(newCurrentId + ider.getFactor() >= newCurrentId, "运算中超过long类型最大值，无法进行计算");
        long anchorId = idProducer.getCurrentId();
        while (anchorId < newCurrentId) {
            int idAmount = calcIdAmountOfPeriod(ider, newCurrentId, anchorId);
            idses.add(buildIds(ider, idProducer, anchorId, idAmount));
            anchorId += ((long) idAmount) * ider.getFactor();
        }
        updateIdProducer(idProducer, ider, newCurrentId);

        return idses;
    }

    // 计算anchorId所在周期内产生的id数量
    private static int calcIdAmountOfPeriod(Ider ider, long newCurrentId, long anchorId) {
        long anchorEndId = newCurrentId;
        if (ider.getMaxId() != null) {
            long anchorMaxId = anchorId / ider.getMaxId() * ider.getMaxId() + ider.getMaxId();
            Assert.isTrue(anchorMaxId >= anchorId, "运算中超过long类型最大值，无法进行计算");
            anchorEndId = Math.min(anchorMaxId, anchorEndId);
        }
        return FacadeUtils.calcTotalPage(anchorEndId - anchorId, ider.getFactor());
    }

    // 构建批量id
    private static Ids buildIds(Ider ider, IdProducer idProducer, long anchorId, int idAmount) {
        Period period = new Period(ider.getPeriodType(), idProducer.getCurrentPeriod());
        long startId = anchorId;
        if (ider.getMaxId() != null) {
            period = period.grow((int) (startId / ider.getMaxId()));
            startId = startId % ider.getMaxId();
        }
        return new Ids(period, ider.getFactor(), startId, idAmount);
    }

    // 更新id生产者
    private static void updateIdProducer(IdProducer idProducer, Ider ider, long newCurrentId) {
        Period period = new Period(ider.getPeriodType(), idProducer.getCurrentPeriod());
        long id = newCurrentId;
        if (ider.getMaxId() != null) {
            period = period.grow((int) (id / ider.getMaxId()));
            id = id % ider.getMaxId();
        }
        idProducer.setCurrentPeriod(period.getDate());
        idProducer.setCurrentId(id);
    }
}
