package com.dji.sample.cloudapi.http;
/**
 * @descirption
 * @name RestTemplateConfig
 * @author Qifei
 * @date 2021-07-07 17:36
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @name RestTemplateConfig
 * @description
 *
 * @author Qfei
 * @date 2021-07-07 17:36
 */
@Slf4j
@Configuration("mapzoneRestTemplateConfiguration")
@EnableConfigurationProperties(HttpPoolProperties.class)
public class RestTemplateConfig {

    @Autowired
    private HttpPoolProperties httpPoolProperties;

    @Bean("mzRestTemplate")
    @ConditionalOnMissingBean
    public RestTemplate mzRestTemplate() {
        return new RestTemplate(httpRequestFactory());
    }

    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    public HttpClient httpClient() {

        log.debug("HttpPoolProperties: " + this.httpPoolProperties.toString());

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", SSLConnectionSocketFactory.getSocketFactory())
                        .build());
        connectionManager.setMaxTotal(httpPoolProperties.maxTotal);
        connectionManager.setDefaultMaxPerRoute(httpPoolProperties.defaultMaxPerRoute);
        connectionManager.setValidateAfterInactivity(httpPoolProperties.validateAfterInactivity);
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setSocketTimeout(httpPoolProperties.socketTimeout)
                        .setConnectTimeout(httpPoolProperties.connectTimeout)
                        .setConnectionRequestTimeout(httpPoolProperties.connectionRequestTimeout)
                        .build())
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setConnectionManager(connectionManager)
                .build();
    }
}
