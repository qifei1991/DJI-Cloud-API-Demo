package com.dji.sdk.cloudapi.interconnection;

import com.dji.sdk.common.BaseModel;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * Speaker play parameter.
 *
 * @author Qfei
 * @date 2024/4/23 18:27
 */
public class SpeakerPlayRequest extends BaseModel {

    @NotNull
    @Range(min = 0, max = 3)
    private Integer psdkIndex;

    @Override
    public String toString() {
        return "SpeakerReplayRequest{" +
                "psdkIndex=" + psdkIndex +
                '}';
    }

    public Integer getPsdkIndex() {
        return psdkIndex;
    }

    public SpeakerPlayRequest setPsdkIndex(Integer psdkIndex) {
        this.psdkIndex = psdkIndex;
        return this;
    }
}
