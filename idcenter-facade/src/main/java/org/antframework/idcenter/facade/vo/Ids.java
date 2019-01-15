/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-15 23:46 创建
 */
package org.antframework.idcenter.facade.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.antframework.common.util.id.Period;
import org.antframework.common.util.tostring.ToString;

import java.io.Serializable;

/**
 * 批量id
 */
@AllArgsConstructor
@Getter
public final class Ids implements Serializable {
    // 周期
    private final Period period;
    // 因数
    private final int factor;
    // 开始id（包含）
    private final long startId;
    // id个数
    private final int amount;

    @Override
    public String toString() {
        return ToString.toString(this);
    }
}
