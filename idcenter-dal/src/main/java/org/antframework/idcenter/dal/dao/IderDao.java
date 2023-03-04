/*
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-21 21:28 创建
 */
package org.antframework.idcenter.dal.dao;

import org.antframework.common.util.query.QueryParam;
import org.antframework.idcenter.dal.entity.Ider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.RepositoryDefinition;

import javax.persistence.LockModeType;
import java.util.Collection;

/**
 * id提供者dao
 */
@RepositoryDefinition(domainClass = Ider.class, idClass = Long.class)
public interface IderDao {

    void save(Ider ider);

    void delete(Ider ider);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Ider findLockByIderId(String iderId);

    Ider findByIderId(String iderId);

    Page<Ider> query(Collection<QueryParam> queryParams, Pageable pageable);
}
