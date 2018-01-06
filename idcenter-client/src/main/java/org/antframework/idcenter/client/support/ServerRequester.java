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
import org.antframework.idcenter.client.core.PeriodType;
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
import java.util.Date;
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
            return toIdsList(result.getIdsInfos());
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

    private List<Ids> toIdsList(List<IdsInfo> idsInfos) {
        List<Ids> idsList = new ArrayList<>();
        for (IdsInfo info : idsInfos) {
            idsList.add(new Ids(info.getIdCode(), new Period(info.getPeriodType(), info.getPeriod()), info.getFactor(), info.getStartId(), info.getAmount()));
        }
        return idsList;
    }

    /**
     * 获取批量id-result
     */
    private static class AcquireIdsResult extends AbstractResult {
        // 获取到的批量id
        private List<IdsInfo> idsInfos;

        public List<IdsInfo> getIdsInfos() {
            return idsInfos;
        }

        public void setIdsInfos(List<IdsInfo> idsInfos) {
            this.idsInfos = idsInfos;
        }
    }

    /**
     * 批量id-info
     */
    private static class IdsInfo extends AbstractInfo {
        // id编码
        private String idCode;
        // 周期类型
        private PeriodType periodType;
        // 因数
        private int factor;
        // 周期
        private Date period;
        // 开始id
        private long startId;
        // id个数
        private int amount;

        public String getIdCode() {
            return idCode;
        }

        public void setIdCode(String idCode) {
            this.idCode = idCode;
        }

        public PeriodType getPeriodType() {
            return periodType;
        }

        public void setPeriodType(PeriodType periodType) {
            this.periodType = periodType;
        }

        public int getFactor() {
            return factor;
        }

        public void setFactor(int factor) {
            this.factor = factor;
        }

        public Date getPeriod() {
            return period;
        }

        public void setPeriod(Date period) {
            this.period = period;
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
