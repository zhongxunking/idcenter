///*
// * 作者：钟勋 (e-mail:zhongxunking@163.com)
// */
//
///*
// * 修订记录:
// * @author 钟勋 2018-01-03 00:19 创建
// */
//package org.antframework.idcenter.test.client;
//
//import org.antframework.idcenter.client.Id;
//import org.antframework.idcenter.client.IdAcquirer;
//import org.antframework.idcenter.client.IdContext;
//import org.antframework.idcenter.client.core.DefaultIdAcquirer;
//import org.antframework.idcenter.client.core.Ids;
//import org.antframework.idcenter.client.support.IdStorage;
//import org.junit.Assert;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.util.ReflectionUtils;
//
//import java.lang.reflect.Field;
//import java.util.Queue;
//import java.util.concurrent.atomic.AtomicLong;
//
///**
// * id上下文单元测试
// */
//@Ignore
//public class IdContextTest {
//    private static final Logger logger = LoggerFactory.getLogger(IdContextTest.class);
//
//    @Test
//    public void testIdContext() throws InterruptedException {
//        IdContext.InitParams initParams = new IdContext.InitParams();
//        initParams.setIdCode("uid");
//        initParams.setServerUrl("http://localhost:6210");
//        initParams.setInitAmount(100);
//        initParams.setMinTime(10 * 60 * 1000);
//        initParams.setMaxTime(15 * 60 * 1000);
//
//        IdContext idContext = new IdContext(initParams);
//        IdAcquirer idAcquirer = idContext.getAcquirer();
//
//        int nullCount = 0;
//
//        for (int i = 0; i < 1000000; i++) {
//            for (int j = 0; j < 10; j++) {
//                Id id = idAcquirer.getId();
//                if (id == null) {
//                    nullCount++;
//                }
//            }
//            Thread.sleep(20);
//            logger.info("----{}----", i);
//            if (nullCount > 0) {
//                logger.error("id出现null次数：{}", nullCount);
//            }
//        }
//        idContext.close();
//    }
//
//    @Test
//    public void testIdContextPerformance() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
//        IdContext.InitParams initParams = new IdContext.InitParams();
//        initParams.setIdCode("uid");
//        initParams.setServerUrl("http://localhost:6210");
//        initParams.setInitAmount(100);
//        initParams.setMinTime(10 * 60 * 1000);
//        initParams.setMaxTime(15 * 60 * 1000);
//
//        IdContext idContext = new IdContext(initParams);
//        IdAcquirer idAcquirer = idContext.getAcquirer();
//
//        for (int j = 0; j < 10; j++) {
//            long startTime = System.currentTimeMillis();
//            int count = 100000000;
//            int nullCount = 0;
//            for (int i = 0; i < count; i++) {
//                Id id = idAcquirer.getId();
//                if (id == null) {
//                    nullCount++;
//                }
//            }
//            long timeCost = System.currentTimeMillis() - startTime;
//            System.out.println(String.format("循环次数：%d，id出现null次数：%d，总耗时：%d毫秒，tps：%d", count, nullCount, timeCost, (count - nullCount) * 1000L / timeCost));
//            Thread.sleep(2000);
//            checkAmount(idContext);
//        }
//    }
//
//    // 校验id余量
//    private void checkAmount(IdContext idContext) throws NoSuchFieldException, IllegalAccessException {
//        DefaultIdAcquirer idAcquirer = (DefaultIdAcquirer) idContext.getAcquirer();
//
//        Field idStorageField = DefaultIdAcquirer.class.getDeclaredField("idStorage");
//        ReflectionUtils.makeAccessible(idStorageField);
//        IdStorage idStorage = (IdStorage) idStorageField.get(idAcquirer);
//
//        Field amountField = IdStorage.class.getDeclaredField("amount");
//        ReflectionUtils.makeAccessible(amountField);
//        AtomicLong amount = (AtomicLong) amountField.get(idStorage);
//
//        Field idsQueueField = IdStorage.class.getDeclaredField("idsQueue");
//        ReflectionUtils.makeAccessible(idsQueueField);
//        Queue<Ids> idsQueue = (Queue<Ids>) idsQueueField.get(idStorage);
//
//        long realAmount = 0;
//        for (Ids ids : idsQueue) {
//            realAmount += ids.getAmount(null);
//        }
//
//        Assert.assertEquals(realAmount, amount.get());
//    }
//}
