package com.dji.sample.control.model.enums;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 相机设置 不同操作的mode值共用枚举
 *
 * @author Qfei
 * @date 2026/3/5 19:16
 */
public enum CameraModeSetEnum {

    ZERO(0),

    ONE(1),

    TWO(2),

    ;

    private final int mode;

    CameraModeSetEnum(int mode) {
        this.mode = mode;
    }

    @JsonValue
    public int getMode() {
        return mode;
    }

    @JsonCreator
    public static CameraModeSetEnum find(int mode) {
        return Arrays.stream(values()).filter(modeEnum -> modeEnum.mode == mode).findAny()
                .orElseThrow(() -> new CloudSDKException(CameraModeSetEnum.class, mode));
    }
}
