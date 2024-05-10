package com.dji.sdk.cloudapi.interconnection;

import com.dji.sdk.common.BaseModel;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * Setting play volume.
 *
 * @author Qfei
 * @date 2024/4/23 18:40
 */
public class SpeakerPlayVolumeSetRequest extends BaseModel {

    @NotNull
    @Range(min = 0, max = 3)
    private Integer psdkIndex;

    @NotNull
    @Range(min = 0, max = 100)
    private Integer playVolume;

    @Override
    public String toString() {
        return "SpeakerPlayVolumeSetRequest{" +
                "psdkIndex=" + psdkIndex +
                ", playVolume=" + playVolume +
                '}';
    }

    public Integer getPsdkIndex() {
        return psdkIndex;
    }

    public SpeakerPlayVolumeSetRequest setPsdkIndex(Integer psdkIndex) {
        this.psdkIndex = psdkIndex;
        return this;
    }

    public Integer getPlayVolume() {
        return playVolume;
    }

    public SpeakerPlayVolumeSetRequest setPlayVolume(Integer playVolume) {
        this.playVolume = playVolume;
        return this;
    }
}
