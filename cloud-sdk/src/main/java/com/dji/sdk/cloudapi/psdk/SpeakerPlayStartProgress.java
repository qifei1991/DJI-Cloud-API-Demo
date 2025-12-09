package com.dji.sdk.cloudapi.psdk;

/**
 * 开始播放进度
 *
 * @author Qfei
 * @date 2025/12/9 18:29
 */
public class SpeakerPlayStartProgress {

    private Integer percent;

    private SpeakerPlayStartStepKeyEnum stepKey;

    @Override
    public String toString() {
        return "SpeakerPlayStartProgress{" +
                "percent=" + percent +
                ", stepKey=" + stepKey +
                '}';
    }

    public Integer getPercent() {
        return percent;
    }

    public SpeakerPlayStartProgress setPercent(Integer percent) {
        this.percent = percent;
        return this;
    }

    public SpeakerPlayStartStepKeyEnum getStepKey() {
        return stepKey;
    }

    public SpeakerPlayStartProgress setStepKey(SpeakerPlayStartStepKeyEnum stepKey) {
        this.stepKey = stepKey;
        return this;
    }
}
