/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-25 17:31 创建
 */
package org.antframework.ids.facade.util;

/**
 * 生产者工具类
 */
public class ProducerUtils {

    /**
     * 解析出生产者编码
     *
     * @param idCode id编码
     * @param index  生产者序号
     * @return 生产者编码
     */
    public static String parseProducerCode(String idCode, int index) {
        return idCode + index;
    }
}
