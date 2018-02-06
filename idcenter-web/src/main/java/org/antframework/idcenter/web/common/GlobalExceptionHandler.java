/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2018-02-03 19:02 创建
 */
package org.antframework.idcenter.web.common;

import org.antframework.common.util.facade.BizException;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.EmptyResult;
import org.antframework.common.util.facade.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * web层异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 处理BizException
    @ExceptionHandler(BizException.class)
    public EmptyResult handleBizException(BizException e) {
        logger.warn("web层捕获到BizException异常：status={}, code={}, message={}", e.getStatus(), e.getCode(), e.getMessage());
        EmptyResult result = new EmptyResult();
        result.setStatus(e.getStatus());
        result.setCode(e.getCode());
        result.setMessage(e.getMessage());

        return result;
    }

    // 处理Exception
    @ExceptionHandler(Exception.class)
    public EmptyResult handleException(Exception e) {
        logger.error("web层捕获到未知异常：", e);
        EmptyResult result = new EmptyResult();
        result.setStatus(Status.PROCESSING);
        result.setCode(CommonResultCode.UNKNOWN_ERROR.getCode());
        result.setMessage(e.getMessage());

        return result;
    }
}
