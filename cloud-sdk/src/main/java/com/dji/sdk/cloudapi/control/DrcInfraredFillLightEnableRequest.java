package com.dji.sdk.cloudapi.control;

import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.common.BaseModel;

import javax.validation.constraints.NotNull;

/**
 * 近红外补光使能
 *
 * @author Qfei
 * @date 2026/3/5 18:47
 */
public class DrcInfraredFillLightEnableRequest extends BaseModel {

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

    public DrcInfraredFillLightEnableRequest() {
    }

    @Override
    public String toString() {
        return "DrcInfraredFillLightEnableRequest{" +
                "payloadIndex=" + payloadIndex +
                ", enable=" + enable +
                '}';
    }

    public PayloadIndex getPayloadIndex() {
        return payloadIndex;
    }

    public DrcInfraredFillLightEnableRequest setPayloadIndex(PayloadIndex payloadIndex) {
        this.payloadIndex = payloadIndex;
        return this;
    }

    public Boolean getEnable() {
        return enable;
    }

    public DrcInfraredFillLightEnableRequest setEnable(Boolean enable) {
        this.enable = enable;
        return this;
    }
}
