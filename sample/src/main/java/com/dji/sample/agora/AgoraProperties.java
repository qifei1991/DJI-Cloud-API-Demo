package com.dji.sample.agora;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Configuration;

/**
 * 声网连接信息
 *
 * @author Qfei
 * @date 2024/5/23 14:38
 */
@Data
@Configuration
@ConfigurationPropertiesBinding
@ConfigurationProperties(prefix = "agora", ignoreInvalidFields = true)
public class AgoraProperties {

    public static String tokenServiceUrl;
    public static Integer tokenExpireTime = 3600;
    public static Integer privilegeExpireTime = 3600;

    public void setTokenServiceUrl(String tokenServiceUrl) {
        AgoraProperties.tokenServiceUrl = tokenServiceUrl;
    }

    public void setTokenExpireTime(Integer tokenExpireTime) {
        AgoraProperties.tokenExpireTime = tokenExpireTime;
    }

    public void setPrivilegeExpireTime(Integer privilegeExpireTime) {
        AgoraProperties.privilegeExpireTime = privilegeExpireTime;
    }
}
