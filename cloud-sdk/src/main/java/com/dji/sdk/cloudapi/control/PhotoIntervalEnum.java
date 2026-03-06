package com.dji.sdk.cloudapi.control;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * @author Qfei
 * @date 2026/3/6 17:13
 */
public enum PhotoIntervalEnum {

    /**
     * {"1":"1s","2":"2s","3":"3s","5":"5s","7":"7s","10":"10s","15":"15s","20":"20s","30":"30s","60":"60s","0.7":"0.7s"}
     */
    SEC_1("1"),
    SEC_2("2"),
    SEC_3("3"),
    SEC_5("5"),
    SEC_7("7"),
    SEC_10("10"),
    SEC_15("15"),
    SEC_20("20"),
    SEC_30("30"),
    SEC_60("60"),
    SEC_0_7("0.7");

    private final String interval;

    PhotoIntervalEnum(String interval) {
        this.interval = interval;
    }

    @JsonValue
    public String getInterval() {
        return interval;
    }

    @JsonCreator
    public static PhotoIntervalEnum find(String value) {
        return Arrays.stream(values()).filter(methodEnum -> methodEnum.interval.equals(value)).findAny()
            .orElseThrow(() -> new CloudSDKException(PhotoIntervalEnum.class, value));
    }
}
