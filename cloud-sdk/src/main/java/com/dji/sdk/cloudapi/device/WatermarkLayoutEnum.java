package com.dji.sdk.cloudapi.device;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 水印位置
 *
 * @author Qfei
 * @date 2026/3/6 16:37
 */
public enum WatermarkLayoutEnum {

    LEFT_UP(0),

    LEFT_DOWN(1),

    RIGHT_UP(2),

    RIGHT_DOWN(3);

    private final int location;

    WatermarkLayoutEnum(int location) {
        this.location = location;
    }

    @JsonValue
    public int getLocation() {
        return location;
    }

    @JsonCreator
    public static WatermarkLayoutEnum find(int location) {
        return Arrays.stream(values()).filter(locationEnum -> locationEnum.location == location).findAny()
                .orElseThrow(() -> new CloudSDKException(WatermarkLayoutEnum.class, location));
    }
}
