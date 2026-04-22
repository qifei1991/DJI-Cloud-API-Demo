package com.dji.sample.wayline;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Configuration;

/**
 * 飞行任务配置
 *
 * @author Qfei
 * @date 2026/4/9 17:28
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "flight-task", ignoreInvalidFields = true)
@ConfigurationPropertiesBinding
public class FlightTaskProperties {

    /**
     * 飞行任务返航时飞机回舱监控任务间隔表达式，复合 cron 表达式语法
     */
    private String returnHomeCron = "0/10 * * * * ?";

    /**
     * 返航监控距离机场水平距离告警阈值，单位米， 默认1米
     */
    private Integer homeDistanceMonitor = 1;

    /**
     * 自动断点续飞条件
     */
    @Autowired
    private BreakPointConditionConfig breakPointCondition;

    /**
     * 阻飞条件
     */
    @Autowired
    private StopFlyingConditionConfig stopFlyingCondition;
}
