package com.dji.sdk.cloudapi.device;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * @author Qfei
 * @date 2025/8/4 14:36
 */
public enum PsdkTypeEnum {

    SPEAKER(5);

    private final int type;

    PsdkTypeEnum(int type) {
        this.type = type;
    }

    @JsonValue
    public int getType() {
        return type;
    }

    @JsonCreator
    public static PsdkTypeEnum find(int type) {
        return Arrays.stream(values())
                .filter(typeEnum -> typeEnum.getType() == type)
                .findFirst()
                .orElseThrow(() -> new CloudSDKException(PsdkTypeEnum.class, type));
    }
}
