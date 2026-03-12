package com.dji.sdk.cloudapi.control;

import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.common.BaseModel;

import javax.validation.constraints.NotNull;

/**
 * 视频分辨率设置
 * <pre>可见光相机分辨率分别为 19201080、38402160。红外相机仅支持 640*512，无法设置</pre>
 *
 * @author Qfei
 * @date 2026/3/6 14:25
 */
public class DrcVideoResolutionSetRequest extends BaseModel {

    /**
     * Camera enumeration.
     * It is unofficial device_mode_key.
     * The format is *{type-subtype-gimbalindex}*.
     * Please read [Product Supported](https://developer.dji.com/doc/cloud-api-tutorial/en/overview/product-support.html)
     */
    @NotNull
    private PayloadIndex payloadIndex;

    @NotNull
    private VideoResolutionEnum videoResolution;

    public DrcVideoResolutionSetRequest() {
    }

    @Override
    public String toString() {
        return "DrcVideoResolutionSetRequest{" +
                "payloadIndex=" + payloadIndex +
                ", videoResolution=" + videoResolution +
                '}';
    }

    public PayloadIndex getPayloadIndex() {
        return payloadIndex;
    }

    public DrcVideoResolutionSetRequest setPayloadIndex(PayloadIndex payloadIndex) {
        this.payloadIndex = payloadIndex;
        return this;
    }

    public VideoResolutionEnum getVideoResolution() {
        return videoResolution;
    }

    public DrcVideoResolutionSetRequest setVideoResolution(VideoResolutionEnum videoResolution) {
        this.videoResolution = videoResolution;
        return this;
    }
}
