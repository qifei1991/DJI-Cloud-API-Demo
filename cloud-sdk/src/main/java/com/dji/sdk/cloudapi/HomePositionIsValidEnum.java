package com.dji.sdk.cloudapi;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 机场home点有效性
 * @author Qfei
 * @date 2026/7/22 18:37
 */
public enum HomePositionIsValidEnum {
    /**
     * {"0":"航向和经纬度坐标都无效","1":"航向和经纬度坐标都有效","2":"航向有效，经纬度无效","3":"经纬度有效，航向无效"}
     */
    INVALID(0, "无效"),

    VALID(1, "有效"),

    HORIZONTAL_INVALID(2, "航向有效，经纬度无效"),

    VERTICAL_INVALID(3, "经纬度有效，航向无效");

    private final int code;
    private final String description;

    HomePositionIsValidEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static HomePositionIsValidEnum fromCode(int code) {
        return Arrays.stream(values())
                .filter(e -> e.code == code)
                .findFirst()
                .orElseThrow(() -> new CloudSDKException(HomePositionIsValidEnum.class, code));
    }
}
