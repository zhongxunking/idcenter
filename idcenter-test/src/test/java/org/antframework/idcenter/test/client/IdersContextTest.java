/*
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-01-03 00:19 创建
 */
package org.antframework.idcenter.test.client;

import org.antframework.common.util.id.Id;
import org.antframework.idcenter.client.Ider;
import org.antframework.idcenter.client.IdersContext;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * id提供者上下文单元测试
 */
@Ignore
public class IdersContextTest {
    private static final Logger logger = LoggerFactory.getLogger(IdersContextTest.class);

    @Test
    public void testIdersContext() throws InterruptedException {
        IdersContext idersContext = new IdersContext("http://localhost:6210", 10 * 60 * 1000, 15 * 60 * 1000);

        Ider ider = idersContext.getIder("userId");
        int nullCount = 0;
        for (int i = 0; i < 1000000; i++) {
            Id id = ider.acquire();
            if (id == null) {
                nullCount++;
            }
        }
        logger.info("id出现null次数：{}", nullCount);
    }
}
