/*
 * 作者：钟勋 (email:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2023-03-06 21:43 创建
 */
package org.antframework.idcenter.web.idshard;

import lombok.extern.slf4j.Slf4j;
import org.antframework.common.util.facade.FacadeUtils;
import org.antframework.idcenter.facade.vo.IdSegment;

import java.util.*;
import java.util.function.BiFunction;

/**
 * id分片中心
 */
@Slf4j
public class IdShardHub {
    // 随机数
    private final Random random = new Random(System.currentTimeMillis());
    // 总分片数
    private volatile int totalShards = 0;
    // 数据源与分片范围的映射集
    private volatile Map<String, List<IdShardRange>> dataSourceShardRanges = new HashMap<>();
    // 数据源列表
    private volatile List<String> dataSources = new ArrayList<>();

    /**
     * 替换总分片数和数据源与分片范围的映射集
     *
     * @param totalShards           总分片数
     * @param dataSourceShardRanges 数据源与分片范围的映射集
     */
    public void replace(int totalShards, Map<String, List<IdShardRange>> dataSourceShardRanges) {
        this.totalShards = totalShards;
        this.dataSourceShardRanges = dataSourceShardRanges;
        List<String> dataSources = new ArrayList<>(dataSourceShardRanges.keySet());
        Collections.sort(dataSources);
        this.dataSources = dataSources;
    }

    /**
     * 获取数据源列表
     *
     * @return 数据源列表
     */
    public List<String> getDataSources() {
        return Collections.unmodifiableList(dataSources);
    }

    /**
     * 获取批量id
     *
     * @param amount     id数量
     * @param idAcquirer id获取器
     * @return 批量id
     */
    public List<IdSegment> acquireIds(int amount, BiFunction<String, Integer, List<IdSegment>> idAcquirer) {
        String dataSource = dataSources.get(random.nextInt(dataSources.size()));
        log.info("选择数据库[{}]提供批量id", dataSource);
        int shardAmount = computeShardAmount(dataSource);
        List<IdSegment> idSegments = idAcquirer.apply(dataSource, FacadeUtils.calcTotalPage(amount, shardAmount));
        return convertIdSegments(dataSource, idSegments);
    }

    // 计算分片数量
    private int computeShardAmount(String dataSource) {
        int amount = 0;
        List<IdShardRange> ranges = dataSourceShardRanges.get(dataSource);
        for (IdShardRange range : ranges) {
            amount += range.getEnd() - range.getStart() + 1;
        }
        return amount;
    }

    // 转换id段集
    private List<IdSegment> convertIdSegments(String dataSource, List<IdSegment> idSegments) {
        List<IdSegment> convertedOne = new ArrayList<>();
        List<IdShardRange> ranges = dataSourceShardRanges.get(dataSource);
        for (IdSegment idSegment : idSegments) {
            for (IdShardRange range : ranges) {
                for (int i = range.getStart(); i <= range.getEnd(); i++) {
                    convertedOne.add(convertIdSegment(idSegment, i));
                }
            }
        }
        return convertedOne;
    }

    // 转换id段
    private IdSegment convertIdSegment(IdSegment idSegment, int shard) {
        int factor = idSegment.getFactor() * totalShards;
        long startId = idSegment.getStartId() * totalShards + shard;
        return new IdSegment(idSegment.getPeriod(), factor, startId, idSegment.getAmount());
    }
}
