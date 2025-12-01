package com.dji.sdk.cloudapi.device;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * tts播放类型
 *
 * @author Qfei
 * @date 2025/11/27 16:52
 */
public enum TtsPlayTypeEnum {

    /**
     * {"0":"男声","1":"女声"}
     */

    MALE(0),

    FEMALE(1);

    private final int type;

    TtsPlayTypeEnum(int type) {
        this.type = type;
    }

    @JsonValue
    public int getType() {
        return type;
    }

    @JsonCreator
    public static TtsPlayTypeEnum find(int type) {
        return Arrays.stream(values())
                .filter(typeEnum -> typeEnum.getType() == type)
                .findFirst()
                .orElseThrow(() -> new CloudSDKException(TtsPlayTypeEnum.class, type));
    }
}
