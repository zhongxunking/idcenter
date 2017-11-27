/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-27 20:09 创建
 */
package org.antframework.ids.facade.info;

import org.antframework.common.util.tostring.ToString;

import java.io.Serializable;

/**
 * 抽象info
 */
public class AbstractInfo implements Serializable {

    @Override
    public String toString() {
        return ToString.toString(this);
    }
}
