package com.dji.sdk.cloudapi.interconnection;

import com.dji.sdk.common.BaseModel;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * 开始播放TTS文本
 *
 * @author Qfei
 * @date 2024/4/23 18:09
 */
public class SpeakerTtsPlayStartRequest extends BaseModel {

    @NotNull
    @Range(min = 0, max = 3)
    private Integer psdkIndex;

    @NotNull
    private PlayTtsFile tts;

    @Override
    public String toString() {
        return "SpeakerTtsPlayStartRequest{" +
                "psdkIndex=" + psdkIndex +
                ", tts=" + tts +
                '}';
    }

    public Integer getPsdkIndex() {
        return psdkIndex;
    }

    public SpeakerTtsPlayStartRequest setPsdkIndex(Integer psdkIndex) {
        this.psdkIndex = psdkIndex;
        return this;
    }

    public PlayTtsFile getTts() {
        return tts;
    }

    public SpeakerTtsPlayStartRequest setTts(PlayTtsFile tts) {
        this.tts = tts;
        return this;
    }
}
