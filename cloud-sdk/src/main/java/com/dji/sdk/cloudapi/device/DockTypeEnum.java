package com.dji.sdk.cloudapi.device;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 机场类型
 *
 * @author Qfei
 * @date 2026/7/22 18:30
 */
public enum DockTypeEnum {
    /**
     * {"landing":"降落","takeoff":"起飞机场"}
     */
    LANDING("landing"),

    TAKEOFF("takeoff");

    private final String value;

    DockTypeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DockTypeEnum find(String value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new CloudSDKException(DockTypeEnum.class, value));
    }
}
