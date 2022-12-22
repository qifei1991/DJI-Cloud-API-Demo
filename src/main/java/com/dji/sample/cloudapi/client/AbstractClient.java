package com.dji.sample.cloudapi.client;

import com.dji.sample.cloudapi.model.vo.ResultView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * 客户端基类
 *
 * @author Qfei
 * @date 2022/12/19 11:55
 */
@Slf4j
public abstract class AbstractClient {

    protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${aircraft-manager-server.url}")
    private String url;
    @Value("${aircraft-manager-server.cloud-api.prefix}")
    private String apiPrefix;
    @Value("${aircraft-manager-server.cloud-api.version:}")
    private String apiVersion;

    @Autowired
    @Qualifier("mzRestTemplate")
    protected RestTemplate mzRestTemplate;

    protected <T> ResultView applicationJsonPost(String uri, T body, Object... uriVariables) {

        String url = this.getManagerServerBaseUrl() + uri;
        log.debug("===> 访问[aircraft-manager], url[{}],\t args[{}],\t uriArgs[{}]", url, body, uriVariables);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<T> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<ResultView> response;
        try {
            response = Objects.isNull(uriVariables)
                     ? this.mzRestTemplate.postForEntity(url, requestEntity, ResultView.class)
                     : this.mzRestTemplate.postForEntity(url, requestEntity, ResultView.class, uriVariables);
        } catch (RestClientException e) {
            log.error("访问[aircraft-manager]失败. ", e);
            throw new RuntimeException("访问[aircraft-manager]失败.");
        }
        ResultView result = response.getBody();
        Assert.notNull(result, "访问[aircraft-manager]响应为空.");

        log.debug("<=== 响应: {}", result);

        return result;
    }


    protected String getManagerServerBaseUrl() {
        return this.url + this.apiPrefix + this.apiVersion;
    }
}
