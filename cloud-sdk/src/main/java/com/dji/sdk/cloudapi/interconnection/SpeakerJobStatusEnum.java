package com.dji.sdk.cloudapi.interconnection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 喊话任务状态
 *
 * @author Qfei
 * @date 2024/8/6 11:21
 */
public enum SpeakerJobStatusEnum {

    PREPARE(0, "准备中"),

    PLAYING(1, "播放中"),

    PLAY_COMPLETE(2, "播放结束"),

    PLAY_START_FAIL(3, "开始播放失败"),

    PLAY_STOP_FAIL(4, "停止播放失败");

    private final int status;

    private final String description;


    SpeakerJobStatusEnum(int status, String description) {
        this.status = status;
        this.description = description;
    }

    @JsonValue
    public int getStatus() {
        return status;
    }

    @JsonCreator
    public static SpeakerJobStatusEnum find(int status) {
        return Arrays.stream(values()).filter(x -> x.getStatus() == status).findAny().orElse(null);
    }
}
