package com.dji.sdk.cloudapi.interconnection;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * @author Qfei
 * @date 2024/4/23 18:03
 */
public enum PlayAudioFormatEnum {

    PCM("pcm"),

    MP3("mp3")

    ;

    private final String format;

    PlayAudioFormatEnum(String format) {
        this.format = format;
    }

    @JsonValue
    public String getFormat() {
        return format;
    }

    @JsonCreator
    public static PlayAudioFormatEnum find(String format) {
        return Arrays.stream(values()).filter(formatEnum -> formatEnum.format.equals(format)).findAny()
                .orElseThrow(() -> new CloudSDKException(PlayAudioFormatEnum.class, format));
    }
}
