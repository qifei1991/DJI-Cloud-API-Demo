package com.dji.sample.psdk.model.param;

import com.dji.sdk.cloudapi.psdk.PlayModeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.Valid;

/**
 * 喊话器设置参数对象
 *
 * @author Qfei
 * @date 2024/8/12 17:55
 */
@Data
@Valid
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SpeakerPlaySetParam extends BasePlaySetParam {

    /**
     * 喊话器播放模式
     * @see com.dji.sdk.cloudapi.psdk.PlayModeEnum
     */
    private PlayModeEnum mode;

    /**
     * 喊话器播放音量
     */
    private Integer volume;

    @Override
    public String toString() {
        return "SpeakerPlaySetParam{" +
                "mode=" + mode +
                ", volume=" + volume +
                ", deviceSn='" + deviceSn + '\'' +
                ", psdkIndex=" + psdkIndex +
                '}';
    }

    @Override
    public SpeakerPlaySetParam setDeviceSn(String deviceSn) {
        super.setDeviceSn(deviceSn);
        return this;
    }

    @Override
    public SpeakerPlaySetParam setPsdkIndex(Integer psdkIndex) {
        super.setPsdkIndex(psdkIndex);
        return this;
    }
}
