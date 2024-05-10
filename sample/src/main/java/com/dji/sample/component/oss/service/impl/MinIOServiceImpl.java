package com.dji.sample.component.oss.service.impl;

import com.dji.sample.component.oss.model.OssConfiguration;
import com.dji.sample.component.oss.service.IOssService;
import com.dji.sdk.cloudapi.storage.CredentialsToken;
import com.dji.sdk.cloudapi.storage.OssTypeEnum;
import io.minio.*;
import io.minio.credentials.AssumeRoleProvider;
import io.minio.credentials.Credentials;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * @author sean
 * @version 0.3
 * @date 2021/12/23
 */
@Service
@Slf4j
public class MinIOServiceImpl implements IOssService {

    private MinioClient client;
    private MinioClient extranetClient;

    @Override
    public OssTypeEnum getOssType() {
        return OssTypeEnum.MINIO;
    }

    @Override
    public CredentialsToken getCredentials() {
        try {
            AssumeRoleProvider provider = new AssumeRoleProvider(OssConfiguration.endpoint, OssConfiguration.accessKey,
                    OssConfiguration.secretKey, Math.toIntExact(OssConfiguration.expire),
                    null, OssConfiguration.region, null, null, null, null);
            Credentials credential = provider.fetch();
            return new CredentialsToken(credential.accessKey(), credential.secretKey(), credential.sessionToken(), OssConfiguration.expire);
        } catch (Exception e) {
            log.error("Failed to obtain minio sts.", e);
        }
        return null;
    }

    @Override
    public URL getObjectUrl(String bucket, String objectKey) {
        try {
            return new URL(
                    extranetClient.getPresignedObjectUrl(
                                    GetPresignedObjectUrlArgs.builder()
                                            .method(Method.GET)
                                            .bucket(bucket)
                                            .object(objectKey)
                                            .expiry(Math.toIntExact(OssConfiguration.expire))
                                            .build()));
        } catch (ErrorResponseException | InsufficientDataException | InternalException |
                InvalidKeyException | InvalidResponseException | IOException |
                NoSuchAlgorithmException | XmlParserException | ServerException e) {
            throw new RuntimeException("The file does not exist on the OssConfiguration.");
        }
    }

    @Override
    public Boolean deleteObject(String bucket, String objectKey) {
        try {
            client.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(objectKey).build());
        } catch (MinioException | NoSuchAlgorithmException | IOException | InvalidKeyException e) {
            log.error("Failed to delete file.");
            return false;
        }
        return true;
    }

    @Override
    public InputStream getObject(String bucket, String objectKey) {
        try {
            GetObjectResponse object = client.getObject(GetObjectArgs.builder().bucket(bucket).object(objectKey).build());
            return new ByteArrayInputStream(object.readAllBytes());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException |
                 IOException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
            log.error("Failed to get file.", e);
        }
        return InputStream.nullInputStream();
    }

    @Override
    public void putObject(String bucket, String objectKey, InputStream input) {
        try {
            client.statObject(StatObjectArgs.builder().bucket(bucket).object(objectKey).build());
            throw new RuntimeException("The filename already exists.");
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.info("The file does not exist, start uploading.");
            try {
                ObjectWriteResponse response = client.putObject(
                        PutObjectArgs.builder().bucket(bucket).object(objectKey).stream(input, input.available(), 0).build());
                log.info("Upload File: {}", response.etag());
            } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException ex) {
                log.error("Failed to upload File {}.", objectKey, ex);
            }
        }
    }

    @Override
    public void createClient() {
        if (Objects.nonNull(this.client)) {
            return;
        }
        this.client = MinioClient.builder()
                .endpoint(OssConfiguration.endpoint)
                .credentials(OssConfiguration.accessKey, OssConfiguration.secretKey)
                .region(OssConfiguration.region)
                .build();

        this.extranetClient = !StringUtils.hasText(OssConfiguration.extranetEndpoint) ? this.client
                : MinioClient.builder()
                    .endpoint(OssConfiguration.extranetEndpoint)
                    .credentials(OssConfiguration.accessKey, OssConfiguration.secretKey)
                    .region(OssConfiguration.region)
                    .build();
    }
}
