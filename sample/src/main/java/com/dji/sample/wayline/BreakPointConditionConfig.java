package com.dji.sample.wayline;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Configuration;

/**
 * 自动断点续飞条件
 *
 * @author Qfei
 * @date 2026/4/22 10:16
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "flight-task.break-point-condition", ignoreInvalidFields = true)
@ConfigurationPropertiesBinding
public class BreakPointConditionConfig {

    /**
     * 是否自动续飞
     */
    private Boolean enabled = true;

    /**
     * 满足自动续飞的最低电量
     */
    private Integer batteryCapacity = 90;

    /**
     * 满足自动续飞的最低风速
     */
    private Float windSpeed = 9F;
}
