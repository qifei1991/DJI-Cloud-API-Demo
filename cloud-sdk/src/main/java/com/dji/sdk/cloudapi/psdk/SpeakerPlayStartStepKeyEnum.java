package com.dji.sdk.cloudapi.psdk;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 开始喊话当前步骤
 *
 * @author Qfei
 * @date 2025/12/9 18:30
 */
public enum SpeakerPlayStartStepKeyEnum {

    /**
     * {"change_work_mode":"切换工作模式","download":"从云端下载音频文件到机场","encoding":"编码pcm为opus","play":"开始播放","upload":"机场上传音频到psdk"}
     */
    CHANGE_WORK_MODE("change_work_mode"),

    DOWNLOAD("download"),

    ENCODING("encoding"),

    PLAY("play"),

    UPLOAD("upload"),
    ;

    private final String step;

    SpeakerPlayStartStepKeyEnum(String step) {
        this.step = step;
    }

    @JsonValue
    public String getStep() {
        return this.step;
    }

    @JsonCreator
    public static SpeakerPlayStartStepKeyEnum of(String step) {
        return Arrays.stream(values())
                .filter(x -> x.step.equals(step))
                .findFirst()
                .orElseThrow(() -> new CloudSDKException(SpeakerPlayStartStepKeyEnum.class, step));
    }
}
