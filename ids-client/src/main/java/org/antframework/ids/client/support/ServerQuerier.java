/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-09 17:25 创建
 */
package org.antframework.ids.client.support;

import com.alibaba.fastjson.JSON;
import org.antframework.ids.client.IdContext;
import org.antframework.ids.facade.info.IdsInfo;
import org.antframework.ids.facade.result.AcquireIdsResult;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务端查询器
 */
public class ServerQuerier {
    // 发送http请求的客户端
    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();
    // 获取批量id的url
    private static final String ACQUIRE_IDS_SUFFIX_URL = "/id/acquireIds";
    // 初始化参数
    private IdContext.InitParams initParams;

    public ServerQuerier(IdContext.InitParams initParams) {
        this.initParams = initParams;
    }

    /**
     * 获取批量id
     *
     * @param expectAmount 期望获取到的id个数
     * @return 批量id
     */
    public List<IdsInfo> acquireIds(int expectAmount) {
        try {
            String resultStr = HTTP_CLIENT.execute(buildRequest(expectAmount), new BasicResponseHandler());
            AcquireIdsResult result = JSON.parseObject(resultStr, AcquireIdsResult.class);
            if (result == null) {
                throw new RuntimeException("请求id中心失败");
            }
            if (!result.isSuccess()) {
                throw new RuntimeException("从id中心获取批量id失败：" + result.getMessage());
            }
            return result.getIdsInfos();
        } catch (IOException e) {
            return ExceptionUtils.rethrow(e);
        }
    }

    // 构建请求
    private HttpUriRequest buildRequest(int expectAmount) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("idCode", initParams.getIdCode()));
        params.add(new BasicNameValuePair("expectAmount", Integer.toString(expectAmount)));

        HttpPost httpPost = new HttpPost(initParams.getServerUrl() + ACQUIRE_IDS_SUFFIX_URL);
        httpPost.setEntity(new UrlEncodedFormEntity(params, Charset.forName("utf-8")));
        return httpPost;
    }
}
