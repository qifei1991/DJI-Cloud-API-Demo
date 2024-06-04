package com.dji.sample.agora.client;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

/**
 * 客户端基类
 *
 * @author Qfei
 * @date 2022/12/19 11:55
 */
@Slf4j
@Component
public abstract class ABaseClient {

    @Autowired
    @Qualifier("mzRestTemplate")
    protected RestTemplate restTemplate;

    public <T> T get(String uri, ParameterizedTypeReference<T> reference, Object... uriVariables) {
        return this.get(uri, MediaType.APPLICATION_FORM_URLENCODED, reference, null, uriVariables);
    }

    public <T> T get(String uri, MediaType mediaType, ParameterizedTypeReference<T> reference, Object... uriVariables) {
        return this.get(uri, mediaType, reference, null, uriVariables);
    }

    public <T> T get(String uri, ParameterizedTypeReference<T> reference, Map<String, Object> params, Object... uriVariables) {
        return this.get(uri, MediaType.APPLICATION_FORM_URLENCODED, reference, params, uriVariables);
    }

    public <T> T get(String uri, MediaType mediaType, ParameterizedTypeReference<T> reference, Map<String, Object> params, Object... uriVariables) {
        return this.formUrlExchange(uri, HttpMethod.GET, mediaType, reference, params, uriVariables);
    }

    public <T> T post(String uri, ParameterizedTypeReference<T> reference, Object... uriVariables) {
        return this.post(uri, MediaType.APPLICATION_FORM_URLENCODED, reference, uriVariables);
    }

    public <T> T post(String uri, MediaType mediaType, ParameterizedTypeReference<T> reference, Object... uriVariables) {
        return this.post(uri, mediaType, reference, null, uriVariables);
    }

    public <T> T post(String uri, ParameterizedTypeReference<T> reference, Map<String, Object> params, Object... uriVariables) {
        return this.post(uri, MediaType.APPLICATION_FORM_URLENCODED, reference, params, uriVariables);
    }

    public <T> T post(String uri, MediaType mediaType, ParameterizedTypeReference<T> reference, Map<String, Object> params, Object... uriVariables) {
        return this.formUrlExchange(uri, HttpMethod.POST, mediaType, reference, params, uriVariables);
    }

    public <T> T postBody(String uri, ParameterizedTypeReference<T> reference, Map<String, Object> params, Object... uriVariables) {
        return this.formBodyExchange(uri, HttpMethod.POST, reference, params, uriVariables);
    }

    public <T> T put(String uri, ParameterizedTypeReference<T> reference, Object... uriVariables) {
        return this.put(uri, MediaType.APPLICATION_FORM_URLENCODED, reference, uriVariables);
    }

    public <T> T put(String uri, MediaType mediaType, ParameterizedTypeReference<T> reference, Object... uriVariables) {
        return this.put(uri, mediaType, reference, null, uriVariables);
    }

    public <T> T put(String uri, ParameterizedTypeReference<T> reference, Map<String, Object> params, Object... uriVariables) {
        return this.put(uri, MediaType.APPLICATION_FORM_URLENCODED, reference, params, uriVariables);
    }

    public <T> T put(String uri, MediaType mediaType, ParameterizedTypeReference<T> reference, Map<String, Object> params, Object... uriVariables) {
        return this.formUrlExchange(uri, HttpMethod.PUT, mediaType, reference, params, uriVariables);
    }

    public <T> T delete(String uri, ParameterizedTypeReference<T> reference, Object... uriVariables) {
        return this.delete(uri, reference, null, uriVariables);
    }

    public <T> T delete(String uri, ParameterizedTypeReference<T> reference, Map<String, Object> params, Object... uriVariables) {
        return this.formUrlExchange(uri, HttpMethod.DELETE, MediaType.APPLICATION_FORM_URLENCODED, reference, params, uriVariables);
    }

    public <T> T formUrlExchange(String uri, HttpMethod method, MediaType mediaType, ParameterizedTypeReference<T> reference,
            Map<String, Object> params, Object... uriVariables) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        this.handleHeader(headers);

        Map<String, Object> args = MapUtil.filter(params, entry -> Objects.nonNull(entry.getValue()));
        String queryString = UrlQuery.of(args).toString();
        if (CharSequenceUtil.isNotBlank(queryString)) {
            uri = uri.concat("?").concat(queryString);
        }
        HttpEntity<MultiValueMap<String, HttpEntity<?>>> requestEntity = new HttpEntity<>(headers);
        return this.exchange(uri, method, requestEntity, reference, uriVariables);
    }

    public <T> T formBodyExchange(String uri, HttpMethod method, ParameterizedTypeReference<T> reference, Map<String, Object> params,
            Object... uriVariables) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        this.handleHeader(headers);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        params.forEach(body::add);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        return this.exchange(uri, method, requestEntity, reference, uriVariables);
    }

    public <T> T uploadExchange(String uri, ParameterizedTypeReference<T> reference,
            MultipartBodyBuilder multipartBodyBuilder,
            Object... uriVariables) {
        return this.uploadExchange(uri, reference, multipartBodyBuilder, null, uriVariables);
    }

    public <T> T uploadExchange(String uri, ParameterizedTypeReference<T> reference,
            MultipartBodyBuilder multipartBodyBuilder, Map<String, Object> params,
            Object... uriVariables) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        this.handleHeader(headers);

        Map<String, Object> args = MapUtil.filter(params, entry -> Objects.nonNull(entry.getValue()));
        args.forEach(multipartBodyBuilder::part);

        HttpEntity<MultiValueMap<String, HttpEntity<?>>> requestEntity = new HttpEntity<>(multipartBodyBuilder.build(), headers);
        return this.exchange(uri, HttpMethod.POST, requestEntity, reference, uriVariables);
    }

    public <T, A> T jsonPostForData(String uri, A body, ParameterizedTypeReference<T> reference, Object... uriVariables) {
        return this.jsonExchange(uri, HttpMethod.POST, body, reference, uriVariables);
    }

    public <T, A> T jsonExchange(String uri, HttpMethod method, A body, ParameterizedTypeReference<T> reference,
            Object... uriVariables) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        this.handleHeader(headers);
        HttpEntity<A> requestEntity = new HttpEntity<>(body, headers);
        return this.exchange(uri, method, requestEntity, reference, uriVariables);
    }

    /**
     * 多重泛型返回值的 Get 请求
     * @param uri URI
     * @param method 请求方法类型
     * @param httpEntity 请求对象
     * @param reference 多重泛型定义
     * @param uriVariables 路径参数
     * @return 多重泛型格式的响应数据
     * @param <T> 响应数据泛型
     */
    public <T, A> T exchange(String uri, HttpMethod method, HttpEntity<A> httpEntity, ParameterizedTypeReference<T> reference,
            Object... uriVariables) {

        String url = this.getRequestUrl(uri);
        if (log.isDebugEnabled()) {
            log.debug("===> Access {}, [{}]: {}", this.getRemoteServiceName(), method, url);
            log.debug("- uriArgs: {}, body: {}", uriVariables, httpEntity.getBody());
        }

        ResponseEntity<T> response;
        try {
            response = Objects.isNull(uriVariables)
                    ? this.restTemplate.exchange(url, method, httpEntity, reference)
                    : this.restTemplate.exchange(url, method, httpEntity, reference, uriVariables);
        } catch (HttpMessageNotReadableException e) {
            log.error("Extract response data fail, Please check the interface response data. URL: {}", url, e);
            throw new RuntimeException("Extract response data fail.");
        } catch (RestClientException e) {
            log.error("Access {} fail, Please check the service usability. URL: {}", this.getRemoteServiceName(), url, e);
            throw new RuntimeException(String.format("Access %s fail.", this.getRemoteServiceName()));
        }
        T result = response.getBody();
        Assert.notNull(result, String.format("The response of access %s is null.", this.getRemoteServiceName()));

        if (log.isDebugEnabled()) {
            String resp = result.toString().length() < 200
                          ? result.toString()
                          : CharSequenceUtil.subPre(result.toString(), 200).concat("...");
            log.debug("<=== Response: {}", resp);
        }
        return result;
    }

    /**
     * 获取客户端服务名称
     */
    protected abstract String getRemoteServiceName();

    /**
     * 获取请求Url地址
     * @param uri URI地址
     * @return 请求Url全地址
     */
    protected abstract String getRequestUrl(String uri);

    /**
     * 创建ObjectMapper转换工具
     * @return ObjectMapper
     */
    protected abstract ObjectMapper getObjectMapper();

    protected void handleHeader(HttpHeaders header) {
    }

}
