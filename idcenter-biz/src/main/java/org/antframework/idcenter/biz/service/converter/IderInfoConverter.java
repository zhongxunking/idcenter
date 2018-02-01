/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-02-01 21:47 创建
 */
package org.antframework.idcenter.biz.service.converter;

import org.antframework.common.util.facade.FacadeUtils;
import org.antframework.idcenter.dal.dao.ProducerDao;
import org.antframework.idcenter.dal.entity.Ider;
import org.antframework.idcenter.dal.entity.Producer;
import org.antframework.idcenter.facade.vo.IderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * id提供者info转换器
 */
@Component
public class IderInfoConverter extends FacadeUtils.DefaultConverter<Ider, IderInfo> {
    // 生产者info转换器
    private static final Converter<Producer, IderInfo.ProducerInfo> PRODUCER_INFO_CONVERTER = new FacadeUtils.DefaultConverter<>(IderInfo.ProducerInfo.class);

    @Autowired
    private ProducerDao producerDao;

    public IderInfoConverter() {
        super(IderInfo.class);
    }

    @Override
    public IderInfo convert(Ider source) {
        IderInfo iderInfo = super.convert(source);
        // 查找id提供者的所有生产者
        List<Producer> producers = producerDao.findByIdCodeOrderByIndexAsc(iderInfo.getIdCode());
        for (Producer producer : producers) {
            iderInfo.addProducerInfo(PRODUCER_INFO_CONVERTER.convert(producer));
        }
        return iderInfo;
    }
}
