/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-25 17:35 创建
 */
package org.antframework.idcenter.dal.dao;

import org.antframework.idcenter.dal.entity.IdProducer;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.RepositoryDefinition;

import javax.persistence.LockModeType;
import java.util.List;

/**
 * id生产者dao
 */
@RepositoryDefinition(domainClass = IdProducer.class, idClass = Long.class)
public interface IdProducerDao {

    void save(IdProducer idProducer);

    void delete(IdProducer idProducer);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    IdProducer findLockByIderIdAndIndex(String iderId, Integer index);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<IdProducer> findLockByIderIdOrderByIndexAsc(String iderId);

    List<IdProducer> findByIderIdOrderByIndexAsc(String iderId);
}
