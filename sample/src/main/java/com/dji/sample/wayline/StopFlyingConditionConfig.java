package com.dji.sample.wayline;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 阻飞条件
 *
 * @author Qfei
 * @date 2026/4/22 10:13
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "flight-task.stop-flying-condition", ignoreInvalidFields = true)
@ConfigurationPropertiesBinding
public class StopFlyingConditionConfig {

    private Boolean enabled = true;

    private Integer windSpeed = 9;

    List<StopFlyingCondition> devices = new ArrayList<>();

    public StopFlyingCondition getDeviceCondition(String sn) {
        return devices.stream()
                .filter(x -> sn.equals(x.getSn()))
                .findFirst()
                .orElse(new StopFlyingCondition()
                        .setSn(sn)
                        .setWindSpeed(windSpeed));
    }
}
