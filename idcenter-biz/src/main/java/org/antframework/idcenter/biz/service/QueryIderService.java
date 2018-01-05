/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-11-27 20:19 创建
 */
package org.antframework.idcenter.biz.service;

import org.antframework.boot.bekit.CommonQueryConstant;
import org.antframework.boot.bekit.CommonQueryResult;
import org.antframework.common.util.facade.FacadeUtils;
import org.antframework.idcenter.biz.util.QueryUtils;
import org.antframework.idcenter.dal.dao.IderDao;
import org.antframework.idcenter.dal.dao.ProducerDao;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.dal.entity.Producer;
import org.antframework.idcenter.facade.info.IderInfo;
import org.antframework.idcenter.facade.order.QueryIderOrder;
import org.antframework.idcenter.facade.result.QueryIderResult;
import org.bekit.service.ServiceEngine;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

/**
 * 查询id提供者服务
 */
@Service
public class QueryIderService {
    @Autowired
    private ServiceEngine serviceEngine;
    @Autowired
    private ProducerDao producerDao;
    // info转换器
    private IderInfoConverter infoConverter = new IderInfoConverter();

    @ServiceExecute
    public void execute(ServiceContext<QueryIderOrder, QueryIderResult> context) {
        QueryIderOrder order = context.getOrder();
        QueryIderResult result = context.getResult();

        CommonQueryResult commonQueryResult = serviceEngine.execute(CommonQueryConstant.SERVICE_NAME, order, QueryUtils.buildCommonQueryAttachment(IderDao.class));
        commonQueryResult.convertTo(result, infoConverter);
    }

    // id提供者info转换器
    private class IderInfoConverter extends FacadeUtils.DefaultConverter<Ider, IderInfo> {
        // 生产者info转换器
        private final Converter<Producer, IderInfo.ProducerInfo> producerInfoConverter = new FacadeUtils.DefaultConverter<>(IderInfo.ProducerInfo.class);

        public IderInfoConverter() {
            super(IderInfo.class);
        }

        @Override
        public IderInfo convert(Ider source) {
            IderInfo iderInfo = super.convert(source);
            // 查找id提供者的所有生产者
            List<Producer> producers = producerDao.findByIdCodeOrderByIndexAsc(iderInfo.getIdCode());
            for (Producer producer : producers) {
                iderInfo.addProducerInfo(producerInfoConverter.convert(producer));
            }
            return iderInfo;
        }
    }
}
