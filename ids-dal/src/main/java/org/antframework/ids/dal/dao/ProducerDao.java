/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-25 17:35 创建
 */
package org.antframework.ids.dal.dao;

import org.antframework.ids.dal.entity.Producer;
import org.springframework.data.repository.RepositoryDefinition;

/**
 * id生产者dao
 */
@RepositoryDefinition(domainClass = Producer.class, idClass = Long.class)
public interface ProducerDao {

    void save(Producer producer);
}
