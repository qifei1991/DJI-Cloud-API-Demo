package com.dji.sdk.cloudapi.interconnection;

import com.dji.sdk.common.BaseModel;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * 开始播放音频请求
 *
 * @author Qfei
 * @date 2024/4/23 17:57
 */
public class SpeakerAudioPlayStartRequest extends BaseModel {

    @NotNull
    @Range(min = 0, max = 3)
    private Integer psdkIndex;

    @NotNull
    private PlayAudioFile file;

    @Override
    public String toString() {
        return "SpeakerAudioPlayStartRequest{" +
                "psdkIndex=" + psdkIndex +
                ", file=" + file +
                '}';
    }

    public Integer getPsdkIndex() {
        return psdkIndex;
    }

    public SpeakerAudioPlayStartRequest setPsdkIndex(Integer psdkIndex) {
        this.psdkIndex = psdkIndex;
        return this;
    }

    public PlayAudioFile getFile() {
        return file;
    }

    public SpeakerAudioPlayStartRequest setFile(PlayAudioFile file) {
        this.file = file;
        return this;
    }
}
