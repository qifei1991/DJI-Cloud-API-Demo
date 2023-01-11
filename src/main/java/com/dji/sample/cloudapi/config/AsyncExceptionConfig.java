package com.dji.sample.cloudapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

/**
 * Configuration the Async Exception Handler.
 *
 * @author Qfei
 * @date 2022/12/22 15:32
 */
@Slf4j
@Configuration
public class AsyncExceptionConfig implements AsyncConfigurer {

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (exp, method, params) -> log.error("Async method [{}] \t Error: {} \t params: {}",
                method.getName(), exp.getMessage(), params, exp);
    }
}
