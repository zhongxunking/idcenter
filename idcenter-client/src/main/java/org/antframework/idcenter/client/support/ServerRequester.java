/*
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-12-09 17:25 创建
 */
package org.antframework.idcenter.client.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.antframework.common.util.facade.AbstractResult;
import org.antframework.common.util.id.Period;
import org.antframework.common.util.id.PeriodType;
import org.antframework.common.util.json.JSON;
import org.antframework.common.util.tostring.ToString;
import org.antframework.manager.client.sign.ManagerSigner;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务端请求器
 */
@AllArgsConstructor
public class ServerRequester {
    // 发送http请求的客户端
    private static final HttpClient HTTP_CLIENT = HttpClients.createDefault();
    // 获取批量id的uri
    private static final String ACQUIRE_IDS_URI = "/ider/acquireIds";
    // 服务端地址
    private final String serverUrl;
    // 管理员签名器
    private final ManagerSigner managerSigner;

    /**
     * 获取批量id
     *
     * @param iderId id提供者的id（id编码）
     * @param amount id数量
     * @return 批量id
     */
    public List<IdSegment> acquireIds(String iderId, int amount) {
        try {
            String resultJson = HTTP_CLIENT.execute(buildRequest(iderId, amount), new BasicResponseHandler());
            AcquireIdsResult result = convertToAcquireIdsResult(resultJson);
            if (result == null) {
                throw new RuntimeException("请求idcenter失败");
            }
            if (!result.isSuccess()) {
                throw new RuntimeException("从idcenter获取批量id失败：" + result.getMessage());
            }
            return result.getIdSegments();
        } catch (IOException e) {
            return ExceptionUtils.rethrow(e);
        }
    }

    // 构建请求
    private HttpUriRequest buildRequest(String iderId, int amount) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("iderId", iderId));
        params.add(new BasicNameValuePair("amount", Integer.toString(amount)));

        HttpPost httpPost = new HttpPost(serverUrl + ACQUIRE_IDS_URI);
        httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
        if (managerSigner != null) {
            managerSigner.sign(httpPost, params);
        }
        return httpPost;
    }

    // 转换成获取批量id-result
    private AcquireIdsResult convertToAcquireIdsResult(String json) throws JsonProcessingException {
        if (json == null) {
            return null;
        }
        AcquireIdsResultInfo resultInfo = JSON.OBJECT_MAPPER.readValue(json, AcquireIdsResultInfo.class);
        List<IdSegment> idSegments = resultInfo.getIdSegments()
                .stream()
                .map(info -> {
                    Period period = new Period(info.getPeriod().getType(), info.getPeriod().getDate());
                    return new IdSegment(period, info.getFactor(), info.getStartId(), info.getAmount());
                })
                .collect(Collectors.toList());

        AcquireIdsResult result = new AcquireIdsResult();
        result.setStatus(resultInfo.getStatus());
        result.setCode(resultInfo.getCode());
        result.setMessage(resultInfo.getMessage());
        result.setIdSegments(idSegments);
        return result;
    }

    /**
     * 获取批量id-result
     */
    @Getter
    @Setter
    public static class AcquireIdsResult extends AbstractResult {
        // id段
        private List<IdSegment> idSegments;
    }

    /**
     * id段
     */
    @AllArgsConstructor
    @Getter
    public static final class IdSegment implements Serializable {
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

    // 获取批量id-result-info
    @Getter
    @Setter
    private static class AcquireIdsResultInfo extends AbstractResult {
        // id段
        private List<IdSegmentInfo> idSegments;
    }

    // id段info
    @Getter
    @Setter
    private static class IdSegmentInfo {
        // 周期
        private PeriodInfo period;
        // 因数
        private int factor;
        // 开始id（包含）
        private long startId;
        // id个数
        private int amount;
    }

    // 周期info
    @Getter
    @Setter
    private static class PeriodInfo {
        // 周期类型
        private PeriodType type;
        // 周期时间
        private Date date;
    }
}
