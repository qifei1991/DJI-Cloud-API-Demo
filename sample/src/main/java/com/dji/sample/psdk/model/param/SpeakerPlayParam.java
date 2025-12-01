package com.dji.sample.psdk.model.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * 喊话器音频播放参数
 *
 * @author Qfei
 * @date 2024/8/12 15:57
 */
@Data
@Valid
public class SpeakerPlayParam {

    /**
     * 喊话内容uuid
     */
    private String contentId;

    /**
     * 喊话任务uuid
     */
    private String jobId;

    /**
     * Dock SN.
     */
    @NotBlank(message = "设备SN不能为空")
    private String deviceSn;

    /**
     * The name of the creator.
     */
    @JsonProperty("user_name")
    private String username;
}
