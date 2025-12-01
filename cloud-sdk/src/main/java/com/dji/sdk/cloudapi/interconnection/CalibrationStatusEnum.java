package com.dji.sdk.cloudapi.interconnection;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * @author Qfei
 * @date 2025/11/27 17:02
 */
public enum CalibrationStatusEnum {

    /**
     * {"0":"校准完成","1":"正在校准","2":"校准失败"}
     */
    COMPLETE(0),

    IN_PROGRESS(1),

    FAILED(2);

    private final int status;

    CalibrationStatusEnum(int status) {
        this.status = status;
    }

    @JsonValue
    public int getStatus() {
        return status;
    }

    @JsonCreator
    public static CalibrationStatusEnum find(int status) {
        return Arrays.stream(values())
                .filter(statusEnum -> statusEnum.getStatus() == status)
                .findFirst()
                .orElseThrow(() -> new CloudSDKException(CalibrationStatusEnum.class, status));
    }
}
