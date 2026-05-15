package com.dji.sample.cloudapi.config;

import cn.hutool.core.collection.CollUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

/**
 * @author Qfei
 * @date 2025/11/18 11:10
 */
@Configuration
@ConfigurationProperties(prefix = "client")
@ConfigurationPropertiesBinding
public class ClientConfig {

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public static String[] excludePath = new String[0];

    public void setExcludePath(String[] excludePath) {
        ClientConfig.excludePath = excludePath;
    }

    public static boolean isExcludePath(String uri) {
        return isExcludePath(excludePath, uri);
    }

    public static boolean isExcludePath(String[] patternArray, String uri) {
        return CollUtil.anyMatch(CollUtil.toList(patternArray), pattern -> antPathMatcher.match(pattern, uri));
    }
}
