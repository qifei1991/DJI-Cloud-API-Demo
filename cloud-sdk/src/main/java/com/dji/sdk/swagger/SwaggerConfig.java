package com.dji.sdk.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SpringDocConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author sean
 * @version 1.7
 * @date 2023/6/14
 */
@Profile("dev") // 仅在开发环境启用Swagger
@Configuration
@OpenAPIDefinition(security = {@SecurityRequirement(name = "default")})
public class SwaggerConfig {

    @Autowired
    private Environment environment;

    @Bean
    public OpenAPI openAPI() {
        // 检查是否为生产环境，如果是则不加载Swagger配置
        if (isProdEnvironment()) {
            return new OpenAPI();
        }

        return new OpenAPI()
                .info(new Info().title("CloudSDK API").description("All HTTP interfaces encapsulated by CloudSDK.")
                        .license(new License().name("LICENSE").url("https://github.com/dji-sdk/DJI-Cloud-API-Demo/blob/main/LICENSE"))
                        .version("1.0.0")).components(components());
    }

    @Bean
    public SecurityScheme securityScheme() {
        // 检查是否为生产环境，如果是则不加载安全配置
        if (isProdEnvironment()) {
            return new SecurityScheme();
        }

        return new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER).name("x-auth-token");
    }

    @Bean
    public Components components() {
        // 检查是否为生产环境，如果是则不加载组件配置
        if (isProdEnvironment()) {
            return new Components();
        }

        return new Components()
                .addSecuritySchemes("default", securityScheme());
    }

    @Bean
    public GroupedOpenApi sdkOpenApi() {
        // 检查是否为生产环境，如果是则不加载API分组配置
        if (isProdEnvironment()) {
            return GroupedOpenApi.builder().group("empty").build();
        }

        return GroupedOpenApi.builder().group("CloudSDK")
                .packagesToScan("com.dji").build();
    }

    @Bean
    public SpringDocConfigProperties springDocConfigProperties() {
        SpringDocConfigProperties properties = new SpringDocConfigProperties();
        // 在生产环境中禁用Swagger UI和API Docs
        if (isProdEnvironment()) {
            properties.getApiDocs().setEnabled(false);
        } else {
            properties.getApiDocs().setEnabled(true);
        }

        properties.setDefaultFlatParamObject(false);
        properties.setDefaultSupportFormData(true);
        properties.setDefaultProducesMediaType("application/json");
        return properties;
    }

    /**
     * 检查是否为生产环境
     * @return 如果是生产环境返回true，否则返回false
     */
    private boolean isProdEnvironment() {
        for (String profile : environment.getActiveProfiles()) {
            if ("dev".equals(profile)) {
                return false;
            }
        }
        return true;
    }
}
