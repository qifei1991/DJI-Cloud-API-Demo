package com.dji.sdk.cloudapi.interconnection;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * @author Qfei
 * @date 2024/4/23 18:34
 */
public enum PlayModeEnum {

    SINGLE(0),

    LOOP(1)

    ;

    private final int mode;

    PlayModeEnum(int mode) {
        this.mode = mode;
    }

    @JsonValue
    public int getMode() {
        return mode;
    }

    @JsonCreator
    public static PlayModeEnum find(int mode) {
        return Arrays.stream(values()).filter(modeEnum -> modeEnum.mode == mode).findAny()
                .orElseThrow(() -> new CloudSDKException(PlayModeEnum.class, mode));
    }
}
