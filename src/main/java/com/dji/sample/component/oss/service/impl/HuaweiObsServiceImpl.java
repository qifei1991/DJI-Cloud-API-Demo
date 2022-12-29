package com.dji.sample.component.oss.service.impl;

import com.aliyun.oss.OSSException;
import com.dji.sample.component.oss.model.OssConfiguration;
import com.dji.sample.component.oss.model.enums.OssTypeEnum;
import com.dji.sample.component.oss.service.IOssService;
import com.dji.sample.media.model.CredentialsDTO;
import com.obs.services.ObsClient;
import com.obs.services.model.HttpMethodEnum;
import com.obs.services.model.PutObjectResult;
import com.obs.services.model.TemporarySignatureRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Objects;

/**
 * @author Qfei
 * @date 2022/12/15 11:45
 */
@Slf4j
@Service
public class HuaweiObsServiceImpl implements IOssService {

    private ObsClient obsClient;

    @Override
    public String getOssType() {
        return OssTypeEnum.OBS.getType();
    }

    @Override
    public CredentialsDTO getCredentials() {

        return new CredentialsDTO();
    }

    @Override
    public URL getObjectUrl(String bucket, String objectKey) {
        boolean exist = this.obsClient.doesObjectExist(bucket, objectKey);
        if (!exist) {
            throw new OSSException("The object does not exist.");
        }
        try {
            return new URL(this.obsClient.createTemporarySignature(new TemporarySignatureRequest(HttpMethodEnum.GET, bucket, objectKey,
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
    }
}
