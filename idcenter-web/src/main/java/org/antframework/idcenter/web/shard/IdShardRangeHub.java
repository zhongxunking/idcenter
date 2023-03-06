/*
 * 作者：钟勋 (email:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2023-03-06 21:43 创建
 */
package org.antframework.idcenter.web.shard;

import org.antframework.common.util.facade.FacadeUtils;
import org.antframework.idcenter.facade.vo.IdSegment;

import java.util.*;
import java.util.function.BiFunction;

/**
 *
 */
public class IdShardRangeHub {

    private final Random random = new Random(System.currentTimeMillis());

    private volatile int totalShards = 0;
    private volatile Map<String, List<IdShardRange>> dataSourceShardRanges = new HashMap<>();

    private volatile List<String> dataSources = new ArrayList<>();

    public void replace(int totalShards, Map<String, List<IdShardRange>> dataSourceShardRanges) {
        this.totalShards = totalShards;
        this.dataSourceShardRanges = dataSourceShardRanges;
        List<String> dataSources = new ArrayList<>(dataSourceShardRanges.keySet());
        Collections.sort(dataSources);
        this.dataSources = dataSources;
    }

    public List<String> getDataSources() {
        return Collections.unmodifiableList(dataSources);
    }

    public List<IdSegment> acquireIds(int amount, BiFunction<String, Integer, List<IdSegment>> idAcquirer) {
        String dataSource = dataSources.get(random.nextInt(dataSources.size()));
        int shardAmount = computeShardAmount(dataSource);
        List<IdSegment> idSegments = idAcquirer.apply(dataSource, FacadeUtils.calcTotalPage(amount, shardAmount));
        return convertIdSegments(dataSource, idSegments);
    }

    private int computeShardAmount(String dataSource) {
        int amount = 0;
        List<IdShardRange> ranges = dataSourceShardRanges.get(dataSource);
        for (IdShardRange range : ranges) {
            amount += range.getEnd() - range.getStart();
        }
        return amount;
    }

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

    private IdSegment convertIdSegment(IdSegment idSegment, int shard) {
        int factor = idSegment.getFactor() * totalShards;
        long startId = idSegment.getStartId() * totalShards + shard;
        return new IdSegment(idSegment.getPeriod(), factor, startId, idSegment.getAmount());
    }
}
