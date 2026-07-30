package com.dji.sdk.cloudapi.device;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * rtk标定设备类型
 * 设备类型	enum_int	{"1":"机场"}
 *
 * @author Qfei
 * @date 2026/7/22 18:45
 */
public enum RtcmDeviceTypeEnum {

    DOCK(1, "机场");

    private final int code;
    private final String description;

    RtcmDeviceTypeEnum(int code, String description) {
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
    public static RtcmDeviceTypeEnum fromCode(int code) {
        return Arrays.stream(values())
                .filter(type -> type.code == code)
                .findFirst()
                .orElseThrow(() -> new CloudSDKException(RtcmDeviceTypeEnum.class, code));
    }
}
