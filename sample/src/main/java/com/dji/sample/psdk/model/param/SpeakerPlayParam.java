package com.dji.sample.psdk.model.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;

/**
 * 喊话器音频播放参数
 *
 * @author Qfei
 * @date 2024/8/12 15:57
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Valid
public class SpeakerPlayParam extends BasePlaySetParam {

    /**
     * 喊话内容uuid
     */
    private String contentId;

    /**
     * 喊话任务uuid
     */
    private String jobId;

    /**
     * The name of the creator.
     */
    @JsonProperty("user_name")
    private String username;

    @Override
    public String toString() {
        return "SpeakerPlayParam{" +
                "contentId='" + contentId + '\'' +
                ", jobId='" + jobId + '\'' +
                ", username='" + username + '\'' +
                ", deviceSn='" + deviceSn + '\'' +
                ", psdkIndex=" + psdkIndex +
                '}';
    }

    @Override
    public SpeakerPlayParam setDeviceSn(String deviceSn) {
        super.setDeviceSn(deviceSn);
        return this;
    }

    @Override
    public SpeakerPlayParam setPsdkIndex(Integer psdkIndex) {
        super.setPsdkIndex(psdkIndex);
        return this;
    }
}
