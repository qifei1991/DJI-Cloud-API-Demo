package com.dji.sample.component.oss.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.aliyun.oss.OSSException;
import com.dji.sample.component.oss.model.OssConfiguration;
import com.dji.sample.component.oss.model.enums.OssTypeEnum;
import com.dji.sample.component.oss.service.IOssService;
import com.dji.sample.media.model.CredentialsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.obs.services.ObsClient;
import com.obs.services.internal.security.SecurityKey;
import com.obs.services.internal.security.SecurityKeyBean;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.HttpMethodEnum;
import com.obs.services.model.PutObjectResult;
import com.obs.services.model.TemporarySignatureRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

/**
 * @author Qfei
 * @date 2022/12/15 11:45
 */
@Slf4j
@Service
public class HuaweiObsServiceImpl implements IOssService {

    @Autowired
    private ObjectMapper mapper;

    public static final String BASE_URL = "https://iam.myhuaweicloud.com";
    public static final String URI_TOKEN = "/v3/auth/tokens";
    public static final String URI_SECURITY_TOKEN = "/v3.0/OS-CREDENTIAL/securitytokens";
    public static final String RESPONSE_TOKEN_PARAM = "X-Subject-Token";

    private SecurityKeyBean securityKey;
    private ObsClient obsClient;
    private ObsClient extranetObsClient;

    @Override
    public String getOssType() {
        return OssTypeEnum.OBS.getType();
    }

    @Override
    public CredentialsDTO getCredentials() {
        try {
            SecurityKeyBean securityKeyBean = this.getSecurityKey();
            return Objects.isNull(securityKeyBean) ? null : new CredentialsDTO(securityKeyBean, OssConfiguration.expire);
        } catch (Exception e) {
            log.debug("Failed to obtain sts.");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public URL getObjectUrl(String bucket, String objectKey) {
        boolean exist = this.obsClient.doesObjectExist(bucket, objectKey);
        if (!exist) {
            throw new OSSException("The object does not exist.");
        }
        try {
            return new URL(this.extranetObsClient.createTemporarySignature(new TemporarySignatureRequest(HttpMethodEnum.GET, bucket, objectKey,
                            null, OssConfiguration.expire, new Date())).getSignedUrl());
        } catch (MalformedURLException e) {
            throw new RuntimeException("The file does not exist on the OssConfiguration.");
        }
    }

    @Override
    public Boolean deleteObject(String bucket, String objectKey) {
        if (!this.obsClient.doesObjectExist(bucket, objectKey)) {
            return true;
        }
        this.obsClient.deleteObject(bucket, objectKey);
        return true;
    }

    @Override
    public InputStream getObject(String bucket, String objectKey) {
        return this.obsClient.getObject(bucket, objectKey).getObjectContent();
    }

    @Override
    public void putObject(String bucket, String objectKey, InputStream input) {
        if (this.obsClient.doesObjectExist(bucket, objectKey)) {
            throw new RuntimeException("The filename already exists.");
        }
        PutObjectResult objectResult = this.obsClient.putObject(bucket, objectKey, input);
        log.info("Upload File: {}", objectResult.getEtag());
    }

    @Override
    public void createClient() {
        if (Objects.nonNull(this.obsClient)) {
            return;
        }

        this.obsClient = new ObsClient(OssConfiguration.accessKey, OssConfiguration.secretKey, OssConfiguration.endpoint);

        this.extranetObsClient = !StringUtils.hasText(OssConfiguration.extranetEndpoint) ? this.obsClient
                : new ObsClient(OssConfiguration.accessKey, OssConfiguration.secretKey, OssConfiguration.extranetEndpoint);
    }

    private SecurityKeyBean getSecurityKey() {
        if (Objects.isNull(securityKey) || isExpired()) {
            securityKey = getSecurityToken();
        }
        return securityKey;
    }

    private boolean isExpired() {
        try {
            Date parse = ServiceUtils.parseIso8601Date(securityKey.getExpiresDate());
            // 提前5秒计算
            return (parse.getTime() - 5000) < System.currentTimeMillis();
        } catch (ParseException e) {
            return true;
        }
    }

    private String getToken() {
        String iamAccount = OssConfiguration.roleArn;
        String[] split = iamAccount.split(StrUtil.SLASH);
        String content = "{\r\n" +
                "			\"auth\": {\r\n" +
                "				\"identity\": {\r\n" +
                "					\"methods\": [\"password\"],\r\n" +
                "					\"password\": {\r\n" +
                "						\"user\": {\r\n" +
                "							\"name\": \"" + split[0] + "\",\r\n" +
                "							\"password\": \"" + split[1] + "\",\r\n" +
                "							\"domain\": {\r\n" +
                "								\"name\": \""+split[2]+"\"\r\n" +
                "							}\r\n" +
                "						}\r\n" +
                "					}\r\n" +
                "				},\r\n" +
                "				\"scope\": {\r\n" +
                "					\"domain\": {\r\n" +
                "						\"name\": \""+split[2]+"\"\r\n" +
                "					}\r\n" +
                "				}\r\n" +
                "			}\r\n" +
                "		  }";
        HttpResponse response = HttpUtil.createPost(BASE_URL + URI_TOKEN)
                .contentType(ContentType.JSON.toString(StandardCharsets.UTF_8))
                .body(content)
                .setConnectionTimeout(10000)
                .setReadTimeout(15000)
                .execute();
        if (response.isOk()) {
            return response.header(RESPONSE_TOKEN_PARAM);
        }
        return null;
    }

    private SecurityKeyBean getSecurityToken() {
        String token = this.getToken();
        if (!StringUtils.hasText(token)) {
            return null;
        }

        String content = "{\r\n" +
                "    \"auth\": {\r\n" +
                "        \"identity\": {\r\n" +
                "            \"methods\": [ \"token\" ],\r\n" +
                "            \"policy\": {\r\n" +
                "                \"Version\": \"1.1\",\r\n" +
                "                \"Statement\": [{\r\n" +
                "                    \"Action\": [\r\n" +
                "                         \"obs:object:PutObject\", \r\n" +
                "                         \"obs:object:GetObject\" \r\n" +
                "                    ],\r\n" +
                "                    \"Resource\": [\r\n" +
                "                         \"obs:*:*:object:"+OssConfiguration.bucket+"/"+OssConfiguration.objectDirPrefix+"/*\" \r\n" +
                "                    ],\r\n" +
                "                    \"Effect\": \"Allow\"\r\n" +
                "                }]\r\n" +
                "            },\n" +
                "            \"token\": {\r\n" +
                "                \"id\": \""+ token +"\",\r\n" +
                "                \"duration-seconds\": \""+ OssConfiguration.expire +"\"\r\n" +
                "            }\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "}";
        HttpResponse response = HttpUtil.createPost(BASE_URL + URI_SECURITY_TOKEN)
                .contentType(ContentType.JSON.toString(StandardCharsets.UTF_8))
                .body(content)
                .setConnectionTimeout(10000)
                .setReadTimeout(15000)
                .execute();
        if (response.isOk()) {
            String body = response.body();
            SecurityKey securityKey = mapper.convertValue(JSONUtil.parseObj(body), SecurityKey.class);
            return securityKey.getBean();
        }
        return null;
    }

}
