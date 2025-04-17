package com.dji.sample.control.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * MQTT ACL rule data.
 *
 * @author Qfei
 * @date 2025/4/14 18:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MqttAclAccessRule {

    @NotNull
    private String action;

    private List<Integer> qos;

    private Boolean retain;

    @Override
    public String toString() {
        return "MqttAclAccessRule{" +
                "action='" + action + '\'' +
                ", qos=" + qos +
                ", retain=" + retain +
                '}';
    }
}
