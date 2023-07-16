package com.dji.sample.configuration;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author Qfei
 * @date 2023/7/14 9:22
 */
@Configuration
public class DruidConfiguration {

    @PostConstruct
    public void setUsePingMethod() {
        System.setProperty("druid.mysql.usePingMethod", "false");
    }
}
