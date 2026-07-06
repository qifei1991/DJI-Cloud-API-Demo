package com.dji.sample.psdk.model.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * 喊话器音频播放参数
 *
 * @author Qfei
 * @date 2024/8/12 15:57
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Valid
@Accessors(chain = true)
public class SpeakerTtsPlayParam extends BasePlaySetParam {

    private String name;

    /**
     * 喊话内容uuid
     */
    @NotBlank
    private String content;

    @Override
    public String toString() {
        return "SpeakerTtsPlayParam{" +
                "deviceSn='" + deviceSn + '\'' +
                ", psdkIndex=" + psdkIndex +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public SpeakerTtsPlayParam setDeviceSn(String deviceSn) {
        super.setDeviceSn(deviceSn);
        return this;
    }

    @Override
    public SpeakerTtsPlayParam setPsdkIndex(Integer psdkIndex) {
        super.setPsdkIndex(psdkIndex);
        return this;
    }
}
