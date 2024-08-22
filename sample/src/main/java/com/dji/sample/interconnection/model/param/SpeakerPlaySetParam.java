package com.dji.sample.interconnection.model.param;

import com.dji.sdk.cloudapi.interconnection.PlayModeEnum;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
     * 设备SN
     */
    @NotBlank
    private String deviceSn;

    @NotNull
    @Range(min = 0, max = 3)
    private Integer psdkIndex = 0;

    /**
     * 喊话器播放模式
     * @see com.dji.sdk.cloudapi.interconnection.PlayModeEnum
     */
    private PlayModeEnum mode;

    /**
     * 喊话器播放音量
     */
    private Integer volume;
}
