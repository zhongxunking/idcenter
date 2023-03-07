/*
 * 作者：钟勋 (email:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2023-03-07 11:35 创建
 */
package org.antframework.idcenter.web.controller.manage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.antframework.common.util.facade.AbstractResult;
import org.antframework.common.util.facade.FacadeUtils;
import org.antframework.idcenter.web.idshard.IdShardHub;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据源管理controller
 */
@RestController
@RequestMapping("/manage/datasource")
@AllArgsConstructor
public class DataSourceManageController {
    // id分片中心
    private final IdShardHub idShardHub;

    /**
     * 查找所有数据源
     *
     * @return 查找结果
     */
    @RequestMapping("/findDataSources")
    public FindDataSourcesResult findDataSources() {
        FindDataSourcesResult result = FacadeUtils.buildSuccess(FindDataSourcesResult.class);
        result.setDataSources(idShardHub.getDataSources());
        return result;
    }

    /**
     * 查找所有数据源result
     */
    @Getter
    @Setter
    public static class FindDataSourcesResult extends AbstractResult {
        // 所有数据源
        private List<String> dataSources;
    }
}
