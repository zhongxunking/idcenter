/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-27 00:34 创建
 */
package org.antframework.idcenter.biz.util;

import org.antframework.common.util.facade.FacadeUtils;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.dal.entity.Producer;
import org.antframework.idcenter.facade.util.PeriodUtils;
import org.antframework.idcenter.facade.vo.IdsInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * id生产者工具类
 */
public class ProducerUtils {

    /**
     * 比较生产者进度
     *
     * @param producer1 待比较的生产者1
     * @param producer2 待比较的生产者2
     * @return 比较结果
     */
    public static int compare(Producer producer1, Producer producer2) {
        if (!StringUtils.equals(producer1.getIdCode(), producer2.getIdCode())) {
            throw new IllegalArgumentException(String.format("待比较的生产者%s和%s不属于同一个id提供者，不能进行比较", producer1.toString(), producer2.toString()));
        }
        if (PeriodUtils.compare(producer1.getCurrentPeriod(), producer2.getCurrentPeriod()) != 0) {
            return PeriodUtils.compare(producer1.getCurrentPeriod(), producer2.getCurrentPeriod());
        }
        if (producer1.getCurrentId() < producer2.getCurrentId()) {
            return -1;
        } else if (producer1.getCurrentId() > producer2.getCurrentId()) {
            return 1;
        }
        return 0;
    }

    /**
     * 生产id
     *
     * @param ider     id提供者
     * @param producer 生产者
     * @param amount   需生产的id个数
     * @return 生产出的id
     */
    public static List<IdsInfo> produce(Ider ider, Producer producer, int amount) {
        return grow(ider, producer, ((long) amount) * ider.getFactor());
    }

    /**
     * 增加
     *
     * @param ider     id提供者
     * @param producer 生产者
     * @param length   增加长度
     * @return 增加过程中产生的id
     */
    public static List<IdsInfo> grow(Ider ider, Producer producer, long length) {
        List<IdsInfo> idsInfos = new ArrayList<>();

        long newCurrentId = producer.getCurrentId() + length;
        Assert.isTrue(newCurrentId >= producer.getCurrentId(), "运算中超过long类型最大值，无法进行计算");
        long anchorId = producer.getCurrentId();
        while (anchorId < newCurrentId) {
            int periodIdAmount = calcPeriodIdAmount(ider, newCurrentId, anchorId);
            idsInfos.add(buildIdsInfo(ider, producer, anchorId, periodIdAmount));
            anchorId += periodIdAmount * ider.getFactor();
        }
        updateProducer(ider, producer, newCurrentId);

        return idsInfos;
    }

    // 计算anchorId所在周期内产生的id数量
    private static int calcPeriodIdAmount(Ider ider, long newCurrentId, long anchorId) {
        long anchorEndId = newCurrentId;
        if (ider.getMaxId() != null) {
            long anchorMaxId = anchorId / ider.getMaxId() * ider.getMaxId() + ider.getMaxId();
            Assert.isTrue(anchorMaxId >= anchorId, "运算中超过long类型最大值，无法进行计算");
            anchorEndId = Math.min(anchorMaxId, newCurrentId);
        }
        return FacadeUtils.calcTotalPage(anchorEndId - anchorId, ider.getFactor());
    }

    // 构建批量id-info
    private static IdsInfo buildIdsInfo(Ider ider, Producer producer, long anchorId, int periodIdAmount) {
        IdsInfo info = new IdsInfo();
        BeanUtils.copyProperties(ider, info);
        if (ider.getMaxId() == null) {
            info.setPeriod(producer.getCurrentPeriod());
            info.setStartId(anchorId);
        } else {
            info.setPeriod(PeriodUtils.grow(ider.getPeriodType(), producer.getCurrentPeriod(), (int) (anchorId / ider.getMaxId())));
            info.setStartId(anchorId % ider.getMaxId());
        }
        info.setAmount(periodIdAmount);

        return info;
    }

    // 更新生产者
    private static void updateProducer(Ider ider, Producer producer, long newCurrentId) {
        if (ider.getMaxId() == null) {
            producer.setCurrentId(newCurrentId);
        } else {
            producer.setCurrentPeriod(PeriodUtils.grow(ider.getPeriodType(), producer.getCurrentPeriod(), (int) (newCurrentId / ider.getMaxId())));
            producer.setCurrentId(newCurrentId % ider.getMaxId());
        }
    }
}
