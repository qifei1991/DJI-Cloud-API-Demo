package com.dji.sdk.cloudapi.control;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * @author Qfei
 * @date 2026/3/5 17:31
 */
public enum CameraNightModeEnum {
    /**
     * {"0":"关闭","1":"开启","2":"自动"}
     */
    OFF(0),

    ON(1),

    AUTO(2);

    private final int mode;

    CameraNightModeEnum(int mode) {
        this.mode = mode;
    }

    @JsonValue
    public int getMode() {
        return mode;
    }

    public static CameraNightModeEnum find(int mode) {
        return Arrays.stream(values()).filter(valueEnum -> valueEnum.mode == mode).findAny()
                .orElseThrow(() -> new CloudSDKException(CameraNightModeEnum.class, mode));
    }
}
