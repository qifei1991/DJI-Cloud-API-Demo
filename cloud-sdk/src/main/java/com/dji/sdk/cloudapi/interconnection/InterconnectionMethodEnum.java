package com.dji.sdk.cloudapi.interconnection;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author sean
 * @version 1.7
 * @date 2023/10/16
 */
public enum InterconnectionMethodEnum {

    CUSTOM_DATA_TRANSMISSION_TO_ESDK("custom_data_transmission_to_esdk"),

    CUSTOM_DATA_TRANSMISSION_TO_PSDK("custom_data_transmission_to_psdk"),

    PSDK_WIDGET_VALUE_SET("psdk_widget_value_set"),

    PSDK_INPUT_BOX_TEXT_SET("psdk_input_box_text_set"),

    SPEAKER_AUDIO_PLAY_START("speaker_audio_play_start"),

    SPEAKER_TTS_PLAY_START("speaker_tss_play_start"),

    SPEAKER_REPLAY("speaker_replay"),

    SPEAKER_PLAY_STOP("speaker_play_stop"),

    SPEAKER_PLAY_MODE_SET("speaker_play_mode_set"),

    SPEAKER_PLAY_VOLUME_SET("speaker_play_volume_set"),

    ;

    private final String method;

    InterconnectionMethodEnum(String method) {
        this.method = method;
    }

    @JsonValue
    public String getMethod() {
        return method;
    }

}
