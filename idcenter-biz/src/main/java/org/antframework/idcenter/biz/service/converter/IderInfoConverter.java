/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-02-01 21:47 创建
 */
package org.antframework.idcenter.biz.service.converter;

import org.antframework.common.util.facade.FacadeUtils;
import org.antframework.idcenter.dal.dao.IdProducerDao;
import org.antframework.idcenter.dal.entity.IdProducer;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.facade.info.IdProducerInfo;
import org.antframework.idcenter.facade.info.IderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * id提供者info转换器
 */
@Component
public class IderInfoConverter extends FacadeUtils.DefaultConverter<Ider, IderInfo> {
    // 生产者info转换器
    private static final Converter<IdProducer, IdProducerInfo> ID_PRODUCER_INFO_CONVERTER = new FacadeUtils.DefaultConverter<>(IdProducerInfo.class);

    @Autowired
    private IdProducerDao idProducerDao;

    public IderInfoConverter() {
        super(IderInfo.class);
    }

    @Override
    public IderInfo convert(Ider source) {
        IderInfo iderInfo = super.convert(source);
        // 查找拥有的id生产者
        List<IdProducerInfo> idProducerInfos = new ArrayList<>();
        for (IdProducer idProducer : idProducerDao.findByIderIdOrderByIndexAsc(iderInfo.getIderId())) {
            idProducerInfos.add(ID_PRODUCER_INFO_CONVERTER.convert(idProducer));
        }
        iderInfo.setIdProducers(idProducerInfos);

        return iderInfo;
    }
}
