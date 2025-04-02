package com.dji.sdk.cloudapi.device;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * @author sean
 * @version 1.4
 * @date 2023/2/28
 */
public enum DockModeCodeEnum {
    /**
     * {"0":"空闲中","1":"现场调试","2":"远程调试","3":"固件升级中","4":"作业中","5":"待标定"}
     */
    IDLE(0),

    DEBUGGING(1),

    REMOTE_DEBUGGING(2),

    UPGRADING(3),

    WORKING(4),

    TO_BE_CALIBRATED(5),    // 待标定

    ;

    private final int code;

    DockModeCodeEnum(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    @JsonCreator
    public static DockModeCodeEnum find(int code) {
        return Arrays.stream(values()).filter(modeCode -> modeCode.code == code).findAny()
                .orElseThrow(() -> new CloudSDKException(DockModeCodeEnum.class, code));
    }
}
