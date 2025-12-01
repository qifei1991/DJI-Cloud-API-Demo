package com.dji.sdk.cloudapi.psdk;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * Setting speaker play mode.
 *
 * @author Qfei
 * @date 2024/4/23 18:31
 */
public class SpeakerPlayModeSetRequest {

    @NotNull
    @Range(min = 0, max = 3)
    private Integer psdkIndex;

    @NotNull
    private PlayModeEnum playMode;

    @Override
    public String toString() {
        return "SpeakerPlayModeSetRequest{" +
                "psdkIndex=" + psdkIndex +
                ", playMode=" + playMode +
                '}';
    }

    public @NotNull @Range(min = 0, max = 3) Integer getPsdkIndex() {
        return psdkIndex;
    }

    public SpeakerPlayModeSetRequest setPsdkIndex(@NotNull @Range(
            min = 0, max = 3) Integer psdkIndex) {
        this.psdkIndex = psdkIndex;
        return this;
    }

    public @NotNull PlayModeEnum getPlayMode() {
        return playMode;
    }

    public SpeakerPlayModeSetRequest setPlayMode(@NotNull PlayModeEnum playMode) {
        this.playMode = playMode;
        return this;
    }
}
