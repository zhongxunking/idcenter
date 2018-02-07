/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-09 17:30 创建
 */
package org.antframework.idcenter.web.controller;

import org.antframework.idcenter.facade.api.IdService;
import org.antframework.idcenter.facade.order.AcquireIdsOrder;
import org.antframework.idcenter.facade.result.AcquireIdsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * id controller
 */
@RestController
@RequestMapping("/id")
public class IdController {
    @Autowired
    private IdService idService;

    /**
     * 获取批量id
     *
     * @param idCode       id编码
     * @param expectAmount 期望获取到的id个数
     */
    @RequestMapping("/acquireIds")
    public AcquireIdsResult acquireIds(String idCode, int expectAmount) {
        AcquireIdsOrder order = new AcquireIdsOrder();
        order.setIdCode(idCode);
        order.setExpectAmount(expectAmount);

        return idService.acquireIds(order);
    }
}
