package com.dji.sdk.cloudapi.control;

import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.cloudapi.device.SwitchActionEnum;
import com.dji.sdk.common.BaseModel;

import javax.validation.constraints.NotNull;

/**
 * 联动变焦参数
 *
 * @author Qfei
 * @date 2026/3/6 15:52
 */
public class DrcLinkageZoomSetRequest extends BaseModel {

    /**
     * Camera enumeration.
     * It is unofficial device_mode_key.
     * The format is *{type-subtype-gimbalindex}*.
     * Please read [Product Supported](https://developer.dji.com/doc/cloud-api-tutorial/en/overview/product-support.html)
     */
    @NotNull
    private PayloadIndex payloadIndex;

    @NotNull
    private SwitchActionEnum state;

    public DrcLinkageZoomSetRequest() {
    }

    @Override
    public String toString() {
        return "DrcLinkageZoomSetRequest{" +
                "payloadIndex=" + payloadIndex +
                ", state=" + state +
                '}';
    }

    public PayloadIndex getPayloadIndex() {
        return payloadIndex;
    }

    public DrcLinkageZoomSetRequest setPayloadIndex(PayloadIndex payloadIndex) {
        this.payloadIndex = payloadIndex;
        return this;
    }

    public SwitchActionEnum getState() {
        return state;
    }

    public DrcLinkageZoomSetRequest setState(SwitchActionEnum state) {
        this.state = state;
        return this;
    }
}
