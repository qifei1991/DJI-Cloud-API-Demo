package com.dji.sdk.cloudapi.control;

import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.common.BaseModel;

import javax.validation.constraints.NotNull;

/**
 * 夜景降噪等级设置
 *
 * @author Qfei
 * @date 2026/3/5 18:15
 */
public class DrcCameraDenoiseLevelSetRequest extends BaseModel {

    /**
     * Camera enumeration.
     * It is unofficial device_mode_key.
     * The format is *{type-subtype-gimbalindex}*.
     * Please read [Product Supported](https://developer.dji.com/doc/cloud-api-tutorial/en/overview/product-support.html)
     */
    @NotNull
    private PayloadIndex payloadIndex;

    @NotNull
    private CameraDenoiseLevelEnum level;

    public DrcCameraDenoiseLevelSetRequest() {
    }

    @Override
    public String toString() {
        return "DrcCameraDenoiseLevelSetRequest{" +
                "payloadIndex=" + payloadIndex +
                ", level=" + level +
                '}';
    }

    public PayloadIndex getPayloadIndex() {
        return payloadIndex;
    }

    public DrcCameraDenoiseLevelSetRequest setPayloadIndex(PayloadIndex payloadIndex) {
        this.payloadIndex = payloadIndex;
        return this;
    }

    public CameraDenoiseLevelEnum getLevel() {
        return level;
    }

    public DrcCameraDenoiseLevelSetRequest setLevel(CameraDenoiseLevelEnum level) {
        this.level = level;
        return this;
    }
}
