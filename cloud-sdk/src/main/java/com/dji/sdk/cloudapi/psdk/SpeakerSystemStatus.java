package com.dji.sdk.cloudapi.psdk;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 喊话器系统状态
 *
 * @author Qfei
 * @date 2025/8/8 16:58
 */
public enum SpeakerSystemStatus {

    /**
     * {"0":"空闲中","1":"传输中(机场到飞行器)","2":"播放中","3":"异常中","4":"TTS 文本转换中","99":"下载中(机场从云端下载)"}
     */
    IDLE(0),

    IN_TRANSMISSION(1),

    PLAYING(2),

    ERROR(3),

    TTS_CONVERTING(4),

    DOWNLOADING_FROM_CLOUD(99);

    private final int status;

    SpeakerSystemStatus(int status) {
        this.status = status;
    }

    @JsonValue
    public int getStatus() {
        return status;
    }

    @JsonCreator
    public static SpeakerSystemStatus find(int status) {
        return Arrays.stream(values())
                .filter(x -> x.status == status)
                .findFirst()
                .orElseThrow(() -> new CloudSDKException(SpeakerWorkMode.class, status));
    }
}
