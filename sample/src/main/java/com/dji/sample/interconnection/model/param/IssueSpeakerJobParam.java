package com.dji.sample.interconnection.model.param;

import com.dji.sdk.cloudapi.interconnection.PlayModeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * 下发喊话器任务参数
 *
 * @author Qfei
 * @date 2024/8/5 14:40
 */
@Valid
@Data
public class IssueSpeakerJobParam {

    /**
     * uuid
     */
    private String contentId;

    /**
     * device SN.
     */
    @NotBlank(message = "接收命令的设备SN不能为空")
    private String deviceSn;

    /**
     * The play content mode.
     * @see com.dji.sdk.cloudapi.interconnection.PlayModeEnum
     */
    private PlayModeEnum mode = PlayModeEnum.SINGLE;

    /**
     * The name of the creator.
     */
    @JsonProperty("user_name")
    private String username;

}
