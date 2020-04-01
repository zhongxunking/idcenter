/* 
 * 作者：钟勋 (email:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2020-04-01 20:46 创建
 */
package org.antframework.idcenter.test.client;

import lombok.extern.slf4j.Slf4j;
import org.antframework.idcenter.client.core.DefaultIder;
import org.antframework.idcenter.client.core.IdChunk;
import org.antframework.idcenter.client.core.IdStorage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ider校验器
 */
@Slf4j
public class IderChecker {
    /**
     * 校验id余量
     *
     * @param ider id提供者
     */
    public static void checkIdAmount(DefaultIder ider) {
        try {
            Thread.sleep(5000);
            Field idStorageField = DefaultIder.class.getDeclaredField("idStorage");
            ReflectionUtils.makeAccessible(idStorageField);
            IdStorage idStorage = (IdStorage) idStorageField.get(ider);

            Field amountField = IdStorage.class.getDeclaredField("amount");
            ReflectionUtils.makeAccessible(amountField);
            AtomicLong amount = (AtomicLong) amountField.get(idStorage);

            Field idChunksField = IdStorage.class.getDeclaredField("idChunks");
            ReflectionUtils.makeAccessible(idChunksField);
            Queue<IdChunk> idChunks = (Queue<IdChunk>) idChunksField.get(idStorage);

            long realAmount = 0;
            for (IdChunk idChunk : idChunks) {
                realAmount += idChunk.getAmount(null);
            }

            log.info("校验IdStorage：id仓库记录余量={}，id真正余量={}，idChunks大小={}", amount.get(), realAmount, idChunks.size());
            Assert.assertEquals(realAmount, amount.get());
        } catch (Throwable e) {
            ExceptionUtils.rethrow(e);
        }
    }

}
