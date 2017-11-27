/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-27 20:19 创建
 */
package org.antframework.ids.biz.service;

import org.antframework.common.util.facade.FacadeUtils;
import org.antframework.ids.dal.dao.IderDao;
import org.antframework.ids.dal.dao.ProducerDao;
import org.antframework.ids.dal.entity.Ider;
import org.antframework.ids.dal.entity.Producer;
import org.antframework.ids.facade.info.IderInfo;
import org.antframework.ids.facade.order.QueryIderOrder;
import org.antframework.ids.facade.result.QueryIderResult;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询id提供者服务
 */
@Service
public class QueryIderService {
    @Autowired
    private IderDao iderDao;
    @Autowired
    private ProducerDao producerDao;

    @ServiceExecute
    public void execute(ServiceContext<QueryIderOrder, QueryIderResult> context) {
        QueryIderOrder order = context.getOrder();

        Page<Ider> page = iderDao.query(buildSearchParams(order), buildPageable(order));
        FacadeUtils.setQueryResult(context.getResult(), new FacadeUtils.SpringDataPageExtractor<>(page), new IderInfoConverter());
    }

    // 构建查询条件
    private Map<String, Object> buildSearchParams(QueryIderOrder queryIderOrder) {
        Map<String, Object> searchParams = new HashMap<>();
        if (queryIderOrder.getIdCode() != null) {
            searchParams.put("LIKE_idCode", "%" + queryIderOrder.getIdCode() + "%");
        }
        if (queryIderOrder.getPeriodType() != null) {
            searchParams.put("EQ_periodType", queryIderOrder.getPeriodType());
        }
        return searchParams;
    }

    // 构建分页
    private Pageable buildPageable(QueryIderOrder queryIderOrder) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return new PageRequest(queryIderOrder.getPageNo() - 1, queryIderOrder.getPageSize(), sort);
    }

    // id提供者信息转换器
    private class IderInfoConverter extends FacadeUtils.DefaultConverter<Ider, IderInfo> {

        public IderInfoConverter() {
            super(IderInfo.class);
        }

        @Override
        public IderInfo convert(Ider source) {
            IderInfo iderInfo = super.convert(source);
            // 查找id提供者的所有生产者
            List<Producer> producers = producerDao.findByIdCodeOrderByIndexAsc(iderInfo.getIdCode());
            for (Producer producer : producers) {
                IderInfo.ProducerInfo producerInfo = new IderInfo.ProducerInfo();
                BeanUtils.copyProperties(producer, producerInfo);
                iderInfo.addProducerInfo(producerInfo);
            }

            return iderInfo;
        }
    }
}
