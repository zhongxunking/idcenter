/*
 * 作者：钟勋 (email:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2023-03-06 18:39 创建
 */
package org.antframework.idcenter.web.idshard;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * id分片范围
 */
@AllArgsConstructor
@Getter
public class IdShardRange {
    // 开始分片（包含）
    private final int start;
    // 结束分片（包含）
    private final int end;
}
