package com.dji.sample.wayline;

import lombok.Data;
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
     * 是否自动续飞
     */
    private Boolean breakPointAuto = true;

    /**
     * 满足自动续飞的最低电量
     */
    private Integer breakPointBatteryCapacity = 80;

    /**
     * 满足自动续飞的最低风速
     */
    private Float breakPointWindSpeed = 10F;
}
