package com.dji.sdk.cloudapi.psdk;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 开始播放处理状态
 *
 * @author Qfei
 * @date 2025/12/9 18:22
 */
public enum SpeakerPlayStartStatus {

    IN_PROGRESS("in_progress"),

    OK("ok")
    ;

    private final String status;

    SpeakerPlayStartStatus(String status) {
        this.status = status;
    }

    @JsonValue
    public String getStatus() {
        return this.status;
    }

    @JsonCreator
    public static SpeakerPlayStartStatus of(String status) {
        return Arrays.stream(values())
                .filter(x -> x.status.equals(status))
                .findFirst()
                .orElseThrow(() -> new CloudSDKException(SpeakerPlayStartStatus.class, status));
    }
}
