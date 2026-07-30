package com.dji.sdk.cloudapi;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 标定类型	enum_int	{"0":"未标定","1":"自收敛标定","2":"手动标定","3":"网络RTK标定"}
 * @author Qfei
 * @date 2026/7/22 18:59
 */
public enum RtcmSourceTypeEnum {

    NOT_CALIBRATED(0, "未标定"),

    SELF_CONVERGENCE_CALIBRATION(1, "自收敛标定"),

    MANUAL_CALIBRATION(2, "手动标定"),

    NETWORK_RTK_CALIBRATION(3, "网络RTK标定");

    private final int value;

    private final String description;

    RtcmSourceTypeEnum(int value, String description) {
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
    public static RtcmSourceTypeEnum fromValue(int value) {
        return Arrays.stream(values())
                .filter(type -> type.value == value)
                .findFirst()
                .orElseThrow(() -> new CloudSDKException(RtcmSourceTypeEnum.class, value));
    }
}
