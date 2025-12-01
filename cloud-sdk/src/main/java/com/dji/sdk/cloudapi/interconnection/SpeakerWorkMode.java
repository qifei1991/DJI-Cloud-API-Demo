package com.dji.sdk.cloudapi.interconnection;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * @author Qfei
 * @date 2025/8/8 16:53
 */
public enum SpeakerWorkMode {

    /**
     * {"0": "TTS模式", "1":"实时喊话","4": "边传边播", "5":"航线喊话"}
     */
    TTS(0),

    REALTIME_SPEAKER(1),

    RECORD_AUDIO(2),

    TRANSFER_AND_PLAY(4),

    WAYLINE_SPEAKER(5),
    ;

    private final Integer mode;

    SpeakerWorkMode(Integer mode) {
        this.mode = mode;
    }

    @JsonValue
    public Integer getMode() {
        return mode;
    }

    @JsonCreator
    public static SpeakerWorkMode find(Integer mode) {
        return Arrays.stream(values())
                .filter(x -> x.mode.equals(mode))
                .findFirst()
                .orElseThrow(() -> new CloudSDKException(SpeakerWorkMode.class, mode));
    }
}
