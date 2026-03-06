package com.dji.sdk.cloudapi.control;

import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.cloudapi.device.SwitchActionEnum;
import com.dji.sdk.common.BaseModel;

import javax.validation.constraints.NotNull;

/**
 * 镜头去畸变
 *
 * @author Qfei
 * @date 2026/3/6 9:26
 */
public class DrcCameraDewarpingSetRequest extends BaseModel {

    /**
     * Camera enumeration.
     * It is unofficial device_mode_key.
     * The format is *{type-subtype-gimbalindex}*.
     * Please read [Product Supported](https://developer.dji.com/doc/cloud-api-tutorial/en/overview/product-support.html)
     */
    @NotNull
    private PayloadIndex payloadIndex;

    @NotNull
    private ExposureCameraTypeEnum cameraType;

    @NotNull
    private SwitchActionEnum dewarpingState;

    public DrcCameraDewarpingSetRequest() {
    }

    @Override
    public String toString() {
        return "DrcCameraDewarpingSetRequest{" +
                "payloadIndex=" + payloadIndex +
                ", cameraType=" + cameraType +
                ", dewarpingState=" + dewarpingState +
                '}';
    }

    public PayloadIndex getPayloadIndex() {
        return payloadIndex;
    }

    public DrcCameraDewarpingSetRequest setPayloadIndex(PayloadIndex payloadIndex) {
        this.payloadIndex = payloadIndex;
        return this;
    }

    public ExposureCameraTypeEnum getCameraType() {
        return cameraType;
    }

    public DrcCameraDewarpingSetRequest setCameraType(ExposureCameraTypeEnum cameraType) {
        this.cameraType = cameraType;
        return this;
    }

    public SwitchActionEnum getDewarpingState() {
        return dewarpingState;
    }

    public DrcCameraDewarpingSetRequest setDewarpingState(SwitchActionEnum dewarpingState) {
        this.dewarpingState = dewarpingState;
        return this;
    }
}
