package com.dji.sample.media.model;

import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.obs.services.model.ISecurityKey;
import io.minio.credentials.Credentials;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author sean
 * @version 0.2
 * @date 2021/12/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialsDTO {

    private static final int DELAY = 300;

    private String accessKeyId;

    private String accessKeySecret;

    private Long expire;

    private String securityToken;

    public CredentialsDTO(Credentials credentials, long expire) {
        this.accessKeyId = credentials.accessKey();
        this.accessKeySecret = credentials.secretKey();
        this.securityToken = credentials.sessionToken();
        this.expire = expire - DELAY;
    }

    public CredentialsDTO(AssumeRoleResponse.Credentials credentials, long expire) {
        this.accessKeyId = credentials.getAccessKeyId();
        this.accessKeySecret = credentials.getAccessKeySecret();
        this.securityToken = credentials.getSecurityToken();
        this.expire = expire - DELAY;
    }

    public CredentialsDTO(com.amazonaws.services.securitytoken.model.Credentials credentials) {
        this.accessKeyId = credentials.getAccessKeyId();
        this.accessKeySecret = credentials.getSecretAccessKey();
        this.securityToken = credentials.getSessionToken();
        this.expire = (credentials.getExpiration().getTime() - System.currentTimeMillis()) / 1000 - DELAY;
    }

    public CredentialsDTO(ISecurityKey securityKey, long expire) {
        this.accessKeyId = securityKey.getAccessKey();
        this.accessKeySecret = securityKey.getSecretKey();
        this.securityToken = securityKey.getSecurityToken();
        this.expire = expire - DELAY;
    }
}
