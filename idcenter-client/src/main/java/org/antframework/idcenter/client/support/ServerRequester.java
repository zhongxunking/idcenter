/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-09 17:25 创建
 */
package org.antframework.idcenter.client.support;

import com.alibaba.fastjson.JSON;
import org.antframework.common.util.facade.AbstractInfo;
import org.antframework.common.util.facade.AbstractResult;
import org.antframework.idcenter.client.IdContext;
import org.antframework.idcenter.client.core.Ids;
import org.antframework.idcenter.client.core.Period;
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务端请求器
 */
public class ServerRequester {
    // 发送http请求的客户端
    private static final HttpClient HTTP_CLIENT = HttpClients.createDefault();
    // 获取批量id的url
    private static final String ACQUIRE_IDS_SUFFIX_URL = "/id/acquireIds";
    // 初始化参数
    private IdContext.InitParams initParams;

    public ServerRequester(IdContext.InitParams initParams) {
        this.initParams = initParams;
    }

    /**
     * 获取批量id
     *
     * @param expectAmount 期望获取到的id个数
     * @return 批量id
     */
    public List<Ids> acquireIds(int expectAmount) {
        try {
            String resultStr = HTTP_CLIENT.execute(buildRequest(expectAmount), new BasicResponseHandler());
            AcquireIdsResult result = JSON.parseObject(resultStr, AcquireIdsResult.class);
            if (result == null) {
                throw new RuntimeException("请求id中心失败");
            }
            if (!result.isSuccess()) {
                throw new RuntimeException("从id中心获取批量id失败：" + result.getMessage());
            }
            return extractIdses(result.getIdses());
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

    // 提取Ids
    private List<Ids> extractIdses(List<IdsInfo> idses) {
        List<Ids> extractedIdses = new ArrayList<>();
        for (IdsInfo info : idses) {
            extractedIdses.add(new Ids(info.getIdCode(), info.getPeriod(), info.getFactor(), info.getStartId(), info.getAmount()));
        }
        return extractedIdses;
    }

    /**
     * 获取批量id-result
     */
    private static class AcquireIdsResult extends AbstractResult {
        // 获取到的批量id
        private List<IdsInfo> idses;

        public List<IdsInfo> getIdses() {
            return idses;
        }

        public void setIdses(List<IdsInfo> idses) {
            this.idses = idses;
        }
    }

    /**
     * 批量id-info
     */
    private static class IdsInfo extends AbstractInfo {
        // id编码
        private String idCode;
        // 周期
        private Period period;
        // 因数
        private int factor;
        // 开始id（包含）
        private long startId;
        // id个数
        private int amount;

        public String getIdCode() {
            return idCode;
        }

        public void setIdCode(String idCode) {
            this.idCode = idCode;
        }

        public Period getPeriod() {
            return period;
        }

        public void setPeriod(Period period) {
            this.period = period;
        }

        public int getFactor() {
            return factor;
        }

        public void setFactor(int factor) {
            this.factor = factor;
        }

        public long getStartId() {
            return startId;
        }

        public void setStartId(long startId) {
            this.startId = startId;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }
}
