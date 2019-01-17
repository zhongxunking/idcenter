/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-01-17 22:48 创建
 */
package org.antframework.idcenter.web.controller;

import org.antframework.idcenter.facade.api.IderService;
import org.antframework.idcenter.facade.order.AcquireIdsOrder;
import org.antframework.idcenter.facade.result.AcquireIdsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * id提供者controller
 */
@RestController
@RequestMapping("/ider")
public class IderController {
    @Autowired
    private IderService iderService;

    /**
     * 获取批量id
     *
     * @param iderId       id提供者的id（id编码）
     * @param expectAmount 期望的数量
     * @return 获取结果
     */
    @RequestMapping("/acquireIds")
    public AcquireIdsResult acquireIds(String iderId, Integer expectAmount) {
        AcquireIdsOrder order = new AcquireIdsOrder();
        order.setIderId(iderId);
        order.setExpectAmount(expectAmount);

        return iderService.acquireIds(order);
    }
}
