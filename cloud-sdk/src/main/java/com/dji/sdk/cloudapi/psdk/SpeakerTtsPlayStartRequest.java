package com.dji.sdk.cloudapi.psdk;

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

    private String jobId;

    @NotNull
    @Range(min = 0, max = 3)
    private Integer psdkIndex;

    @NotNull
    private PlayTtsFile tts;

    @Override
    public String toString() {
        return "SpeakerTtsPlayStartRequest{" +
                "jobId='" + jobId + '\'' +
                ", psdkIndex=" + psdkIndex +
                ", tts=" + tts +
                '}';
    }

    public String getJobId() {
        return jobId;
    }

    public SpeakerTtsPlayStartRequest setJobId(String jobId) {
        this.jobId = jobId;
        return this;
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
