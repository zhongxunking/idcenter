/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-09 17:25 创建
 */
package org.antframework.idcenter.client.support;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.antframework.common.util.facade.AbstractResult;
import org.antframework.common.util.id.Period;
import org.antframework.common.util.tostring.ToString;
import org.antframework.idcenter.client.core.Ids;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务端请求器
 */
public class ServerRequester {
    // 发送http请求的客户端
    private static final HttpClient HTTP_CLIENT = HttpClients.createDefault();
    // 获取批量id的url后缀
    private static final String ACQUIRE_IDS_URL_SUFFIX = "/ider/acquireIds";
    // 获取批量id的url
    private final String acquireIdsUrl;

    /**
     * 构造服务端请求器
     *
     * @param serverUrl 服务端地址
     */
    public ServerRequester(String serverUrl) {
        this.acquireIdsUrl = serverUrl + ACQUIRE_IDS_URL_SUFFIX;
    }

    /**
     * 获取批量id
     *
     * @param iderId       id提供者的id（id编码）
     * @param expectAmount 期望获取到的id个数
     * @return 批量id
     */
    public List<Ids> acquireIds(String iderId, int expectAmount) {
        try {
            String resultStr = HTTP_CLIENT.execute(buildRequest(iderId, expectAmount), new BasicResponseHandler());
            AcquireIdsResult result = JSON.parseObject(resultStr, AcquireIdsResult.class);
            if (result == null) {
                throw new RuntimeException("请求id中心失败");
            }
            if (!result.isSuccess()) {
                throw new RuntimeException("从id中心获取批量id失败：" + result.getMessage());
            }
            return convert(result.getIdses());
        } catch (IOException e) {
            return ExceptionUtils.rethrow(e);
        }
    }

    // 构建请求
    private HttpUriRequest buildRequest(String iderId, int expectAmount) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("iderId", iderId));
        params.add(new BasicNameValuePair("expectAmount", Integer.toString(expectAmount)));

        HttpPost httpPost = new HttpPost(acquireIdsUrl);
        httpPost.setEntity(new UrlEncodedFormEntity(params, Charset.forName("utf-8")));
        return httpPost;
    }

    // 转换
    private List<Ids> convert(List<AcquireIdsResult.Ids> idses) {
        List<Ids> convertedIdses = new ArrayList<>();
        for (AcquireIdsResult.Ids ids : idses) {
            convertedIdses.add(new Ids(ids.getPeriod(), ids.getFactor(), ids.getStartId(), ids.getAmount()));
        }
        return convertedIdses;
    }

    /**
     * 获取批量id-result
     */
    @Getter
    @Setter
    private static class AcquireIdsResult extends AbstractResult {
        // 获取到的批量id
        private List<Ids> idses;

        /**
         * 批量id
         */
        @AllArgsConstructor
        @Getter
        private static final class Ids implements Serializable {
            // 周期
            private final Period period;
            // 因数
            private final int factor;
            // 开始id（包含）
            private final long startId;
            // id个数
            private final int amount;

            @Override
            public String toString() {
                return ToString.toString(this);
            }
        }
    }
}
