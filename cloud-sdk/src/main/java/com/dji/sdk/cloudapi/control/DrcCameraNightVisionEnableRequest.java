package com.dji.sdk.cloudapi.control;

import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.common.BaseModel;

import javax.validation.constraints.NotNull;

/**
 * 黑白夜视使能
 *
 * @author Qfei
 * @date 2026/3/5 18:44
 */
public class DrcCameraNightVisionEnableRequest extends BaseModel {

    /**
     * Camera enumeration.
     * It is unofficial device_mode_key.
     * The format is *{type-subtype-gimbalindex}*.
     * Please read [Product Supported](https://developer.dji.com/doc/cloud-api-tutorial/en/overview/product-support.html)
     */
    @NotNull
    private PayloadIndex payloadIndex;

    @NotNull
    private Boolean enable;

    public DrcCameraNightVisionEnableRequest() {
    }

    @Override
    public String toString() {
        return "DrcCameraNightVisionEnableRequest{" +
                "payloadIndex=" + payloadIndex +
                ", enable=" + enable +
                '}';
    }

    public PayloadIndex getPayloadIndex() {
        return payloadIndex;
    }

    public DrcCameraNightVisionEnableRequest setPayloadIndex(PayloadIndex payloadIndex) {
        this.payloadIndex = payloadIndex;
        return this;
    }

    public Boolean getEnable() {
        return enable;
    }

    public DrcCameraNightVisionEnableRequest setEnable(Boolean enable) {
        this.enable = enable;
        return this;
    }
}
