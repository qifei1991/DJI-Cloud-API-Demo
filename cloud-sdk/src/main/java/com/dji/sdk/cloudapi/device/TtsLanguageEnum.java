package com.dji.sdk.cloudapi.device;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * tts语言
 *
 * @author Qfei
 * @date 2025/11/27 16:56
 */
public enum TtsLanguageEnum {

    /**
     * {"0":"中文","1":"英文"}
     */
    CHINESE(0),

    ENGLISH(1);

    private final int language;

    TtsLanguageEnum(int language) {
        this.language = language;
    }

    @JsonValue
    public int getLanguage() {
        return language;
    }

    @JsonCreator
    public static TtsLanguageEnum find(int language) {
        return Arrays.stream(values())
                .filter(typeEnum -> typeEnum.getLanguage() == language)
                .findFirst()
                .orElseThrow(() -> new CloudSDKException(TtsLanguageEnum.class, language));
    }
}
