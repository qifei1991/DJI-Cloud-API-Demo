package com.dji.sample.interconnection.model.enums;

import com.dji.sdk.cloudapi.device.DeviceDomainEnum;
import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 喊话器内容类型
 *
 * @author Qfei
 * @date 2024/4/24 15:55
 */
public enum SpeakerContentTypeEnum {

    TTS(0),

    AUDIO(1)

    ;

    private final int type;

    SpeakerContentTypeEnum(int type) {
        this.type = type;
    }

    @JsonValue
    public int getType() {
        return this.type;
    }

    @JsonCreator
    public static SpeakerContentTypeEnum find(int type) {
        return Arrays.stream(values()).filter(typeEnum -> typeEnum.getType() == type).findAny()
                .orElseThrow(() -> new CloudSDKException(DeviceDomainEnum.class, type));
    }
}
