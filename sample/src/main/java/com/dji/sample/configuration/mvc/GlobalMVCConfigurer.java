package com.dji.sample.configuration.mvc;

import com.dji.sample.component.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class GlobalMVCConfigurer implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;
    @Autowired
    private Environment environment;

    private static List<String> excludePaths = new ArrayList<>();

    @Value("${url.manage.prefix}")
    private String managePrefix;

    @Value("${url.manage.version}")
    private String manageVersion;

    @Value("${url.cloud-api.prefix:/cloud-api}")
    private String apiPrefix;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Exclude the login interface.
        excludePaths.add("/" + managePrefix + manageVersion + "/login");
        excludePaths.add("/" + managePrefix + manageVersion + "/token/refresh");
        if (!isProdEnvironment()) {
            excludePaths.add("/swagger-ui.html");
            excludePaths.add("/swagger-ui/**");
            excludePaths.add("/v3/**");
            excludePaths.add("/ui/**");
        }
        // Exclude the cloud-api interface.
        excludePaths.add(apiPrefix + "/**");
        excludePaths.add("/updatelogs.html");
        // Intercept for all request interfaces.
        registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns(excludePaths);
    }

    /**
     * 检查是否为生产环境
     * @return 如果是生产环境返回true，否则返回false
     */
    public boolean isProdEnvironment() {
        for (String profile : environment.getActiveProfiles()) {
            if ("dev".equals(profile)) {
                return false;
            }
        }
        return true;
    }
}
