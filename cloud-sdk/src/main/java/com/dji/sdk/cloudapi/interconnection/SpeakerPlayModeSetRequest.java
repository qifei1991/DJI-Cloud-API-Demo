package com.dji.sdk.cloudapi.interconnection;

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
}
