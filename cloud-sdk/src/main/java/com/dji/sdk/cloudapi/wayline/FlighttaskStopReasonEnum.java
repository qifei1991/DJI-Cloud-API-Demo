package com.dji.sdk.cloudapi.wayline;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 飞行基本结束原因
 *
 * @author Qfei
 * @date 2026/7/27 16:37
 */
public enum FlighttaskStopReasonEnum {
    /**
     * {"0":"正常结束","1":"另一机场状态机异常"}
     */
    NORMAL(0),

    OTHER_DOCK_STATUS_ERROR(1);

    private final int code;

    FlighttaskStopReasonEnum(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    @JsonCreator
    public static FlighttaskStopReasonEnum find(int code) {
        return Arrays.stream(values()).filter(e -> e.code == code)
                .findFirst().orElseThrow(() -> new CloudSDKException(FlighttaskBreakReasonEnum.class, code));
    }

    public static FlighttaskStopReasonEnum getDockReason(int code) {
        return NORMAL.getCode() == code ? NORMAL : OTHER_DOCK_STATUS_ERROR;
    }
}
