package com.dji.sdk.cloudapi.control;

import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.common.BaseModel;

import javax.validation.constraints.NotNull;

/**
 * 夜景模式设置
 *
 * @author Qfei
 * @date 2026/3/5 17:30
 */
public class DrcCameraNightModeSetRequest extends BaseModel {

    /**
     * Camera enumeration.
     * It is unofficial device_mode_key.
     * The format is *{type-subtype-gimbalindex}*.
     * Please read [Product Supported](https://developer.dji.com/doc/cloud-api-tutorial/en/overview/product-support.html)
     */
    @NotNull
    private PayloadIndex payloadIndex;

    @NotNull
    private CameraNightModeEnum mode;

    public DrcCameraNightModeSetRequest() {
    }

    @Override
    public String toString() {
        return "DrcCameraNightModeSetRequest{" +
                "payloadIndex=" + payloadIndex +
                ", mode=" + mode +
                '}';
    }

    public PayloadIndex getPayloadIndex() {
        return payloadIndex;
    }

    public DrcCameraNightModeSetRequest setPayloadIndex(PayloadIndex payloadIndex) {
        this.payloadIndex = payloadIndex;
        return this;
    }

    public CameraNightModeEnum getMode() {
        return mode;
    }

    public DrcCameraNightModeSetRequest setMode(CameraNightModeEnum mode) {
        this.mode = mode;
        return this;
    }
}
