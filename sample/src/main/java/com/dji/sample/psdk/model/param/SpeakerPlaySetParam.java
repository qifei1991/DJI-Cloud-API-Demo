package com.dji.sample.psdk.model.param;

import com.dji.sdk.cloudapi.psdk.PlayModeEnum;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * 喊话器设置参数对象
 *
 * @author Qfei
 * @date 2024/8/12 17:55
 */
@Data
@Valid
public class SpeakerPlaySetParam {

    /**
     * 机场设备SN
     */
    @NotBlank
    private String deviceSn;

    /**
     * 喊话器播放模式
     * @see com.dji.sdk.cloudapi.psdk.PlayModeEnum
     */
    private PlayModeEnum mode;

    /**
     * 喊话器播放音量
     */
    private Integer volume;
}
