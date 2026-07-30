package com.dji.sdk.cloudapi.device;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 是否设置备降点	enum_int	{"0":"未设置","1":"已设置"}
 * @author Qfei
 * @date 2026/7/22 18:56
 */
public enum ConfiguredEnum {

    NOT_CONFIGURED(0, "未设置"),

    CONFIGURED(1, "已设置");

    private final int value;

    private final String description;

    ConfiguredEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static ConfiguredEnum fromValue(int value) {
        return Arrays.stream(values())
                .filter(e -> e.value == value)
                .findFirst()
                .orElseThrow(() -> new CloudSDKException(ConfiguredEnum.class, value));
    }
}
