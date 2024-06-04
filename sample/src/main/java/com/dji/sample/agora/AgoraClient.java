package com.dji.sample.agora;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.dji.sample.agora.model.GenerateTokenDTO;
import com.dji.sample.agora.model.ResultData;
import com.dji.sample.agora.model.TokenResult;
import com.dji.sample.agora.client.ABaseClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

/**
 * @author Qfei
 * @date 2024/5/23 14:16
 */
@Component
@RequiredArgsConstructor
public class AgoraClient extends ABaseClient {

    // 生成Token
    private static final String URI_TOKEN = "/token/generate";

    public ResultData<TokenResult> generateToken(GenerateTokenDTO tokenDTO) {
        return super.jsonPostForData(URI_TOKEN, tokenDTO, new ParameterizedTypeReference<ResultData<TokenResult>>() {});
    }

    /**
     * @return
     */
    @Override
    protected String getRemoteServiceName() {
        return "[AgoraTokenService]";
    }

    /**
     * @param uri URI地址
     * @return
     */
    @Override
    protected String getRequestUrl(String uri) {
        if (StrUtil.isBlank(AgoraProperties.tokenServiceUrl)) {
            throw new RuntimeException("请配置Agora的Token服务地址[agora:token-service-url]");
        }
        return StrUtil.removeSuffix(AgoraProperties.tokenServiceUrl, StrPool.SLASH)  + uri;
    }

    /**
     * @return
     */
    @Override
    protected ObjectMapper getObjectMapper() {
        return null;
    }
}
