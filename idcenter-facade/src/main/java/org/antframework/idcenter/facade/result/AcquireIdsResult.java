/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-29 22:45 创建
 */
package org.antframework.idcenter.facade.result;

import lombok.Getter;
import lombok.Setter;
import org.antframework.common.util.facade.AbstractResult;
import org.antframework.idcenter.facade.vo.Ids;

import java.util.List;

/**
 * 获取批量id-result
 */
@Getter
@Setter
public class AcquireIdsResult extends AbstractResult {
    // 获取到的批量id
    private List<Ids> idses;
}
