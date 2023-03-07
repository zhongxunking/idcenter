/*
 * 作者：钟勋 (email:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2023-03-06 17:40 创建
 */
package org.antframework.idcenter.web.idshard;

import lombok.AllArgsConstructor;
import org.antframework.datasource.core.DataSourceProperty;
import org.antframework.datasource.core.DataSourcePropertyFilter;
import org.antframework.idcenter.web.WebConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * id分片数据源配置过滤器
 */
@AllArgsConstructor
public class IdShardDataSourcePropertyFilter implements DataSourcePropertyFilter {
    /**
     * id分片范围集的key
     */
    public static final String ID_SHARD_RANGES_KEY = "idcenter.id-shard-ranges";

    // id分片中心
    private final IdShardHub idShardHub;
    // 配置
    private final WebConfiguration.IdcenterProperties properties;

    @Override
    public int getOrder() {
        return properties.getIdShard().getIdShardDataSourcePropertyOrder();
    }

    @Override
    public Map<String, DataSourceProperty> doFilter(Map<String, DataSourceProperty> nameProperties) {
        Map<String, DataSourceProperty> filteredOne = new HashMap<>();

        computeIdShardRanges(nameProperties);
        Map<String, List<IdShardRange>> nameRanges = new HashMap<>();
        for (String datasource : properties.getIdShard().getActiveDatasources()) {
            DataSourceProperty property = nameProperties.get(datasource);
            Assert.notNull(property, String.format("数据源[%s]不存在", datasource));
            List<IdShardRange> ranges = (List<IdShardRange>) property.getMetadata().get(ID_SHARD_RANGES_KEY);
            Assert.notNull(ranges, String.format("数据源[%s]未配置元数据idcenter.id-shard-ranges", datasource));
            nameRanges.put(datasource, ranges);
            filteredOne.put(datasource, property);
        }
        idShardHub.replace(properties.getIdShard().getTotalShards(), nameRanges);

        return filteredOne;
    }

    // 计算id分片范围集
    private void computeIdShardRanges(Map<String, DataSourceProperty> nameProperties) {
        nameProperties.forEach((name, property) -> {
            String rangesStr = (String) property.getMetadata().get(ID_SHARD_RANGES_KEY);
            if (rangesStr != null) {
                List<IdShardRange> ranges = convertRanges(rangesStr);
                property.getMetadata().put(ID_SHARD_RANGES_KEY, ranges);
            }
        });
    }

    // 转换id分片范围集
    private List<IdShardRange> convertRanges(String rangesStr) {
        List<IdShardRange> ranges = new ArrayList<>();
        String[] rangeStrs = StringUtils.split(rangesStr, ';');
        for (String rangeStr : rangeStrs) {
            IdShardRange range = convertRange(rangeStr);
            ranges.forEach(one -> Assert.isTrue(range.getStart() > one.getEnd() || range.getEnd() < one.getStart(), String.format("数据源的元数据idcenter.id-shard-ranges的配置[%s]不合法，与其他数据源的分片范围有重合", rangesStr)));
            ranges.add(range);
        }
        return ranges;
    }

    // 转换id分片范围
    private IdShardRange convertRange(String rangeStr) {
        String[] startEnd = StringUtils.split(rangeStr, ',');
        Assert.isTrue(startEnd.length == 2, String.format("数据源的元数据idcenter.shard-ranges的配置[%s]不合法", rangeStr));
        int start = Integer.parseInt(startEnd[0].trim());
        int end = Integer.parseInt(startEnd[1].trim());
        Assert.isTrue(start >= 0 && start <= end && end < properties.getIdShard().getTotalShards(), String.format("数据源的元数据idcenter.id-shard-ranges的配置[%s]不合法", rangeStr));
        return new IdShardRange(start, end);
    }
}
