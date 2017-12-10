/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-09 17:30 创建
 */
package org.antframework.ids.web.controller;

import org.antframework.ids.facade.api.IderService;
import org.antframework.ids.facade.order.AcquireIdsOrder;
import org.antframework.ids.facade.result.AcquireIdsResult;
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
    private IderService iderService;

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

        return iderService.acquireIds(order);
    }
}
