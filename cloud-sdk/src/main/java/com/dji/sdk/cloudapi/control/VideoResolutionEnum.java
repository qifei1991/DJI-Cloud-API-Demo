package com.dji.sdk.cloudapi.control;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * @author Qfei
 * @date 2026/3/6 14:28
 */
public enum VideoResolutionEnum {

    RES_1920_1080("0"),

    RES_3840_2160("1");

    private final String value;

    VideoResolutionEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static VideoResolutionEnum find(String value) {
        return Arrays.stream(values()).filter(valueEnum -> valueEnum.value.equals(value)).findAny()
                .orElseThrow(() -> new CloudSDKException(VideoResolutionEnum.class, value));
    }
}
