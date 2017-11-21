/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-21 21:28 创建
 */
package org.antframework.ids.dal.dao;

import org.antframework.ids.dal.entity.Ider;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.RepositoryDefinition;

import javax.persistence.LockModeType;

/**
 * id提供则dao
 */
@RepositoryDefinition(domainClass = Ider.class, idClass = Long.class)
public interface IderDao {

    void save(Ider ider);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Ider findLockByIdCode(String idCode);
}
