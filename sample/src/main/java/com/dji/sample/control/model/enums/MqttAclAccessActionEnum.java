package com.dji.sample.control.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * MQTT ACL
 *
 * @author Qfei
 * @date 2025/4/14 18:17
 */
public enum MqttAclAccessActionEnum {

    SUBSCRIBE("subscribe"),

    PUBLISH("publish"),

    ALL("all");

    @JsonValue
    private final String action;

    MqttAclAccessActionEnum(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

}
