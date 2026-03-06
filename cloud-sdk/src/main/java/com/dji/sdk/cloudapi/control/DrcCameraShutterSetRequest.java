package com.dji.sdk.cloudapi.control;

import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.cloudapi.device.ShutterSpeedEnum;
import com.dji.sdk.common.BaseModel;

import javax.validation.constraints.NotNull;

/**
 * 相机快门设置
 *
 * @author Qfei
 * @date 2026/3/5 16:18
 */
public class DrcCameraShutterSetRequest extends BaseModel {

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
    private ShutterSpeedEnum shutterValue;

    public DrcCameraShutterSetRequest() {
    }

    @Override
    public String toString() {
        return "DrcCameraShutterSetRequest{" +
                "payloadIndex=" + payloadIndex +
                ", cameraType=" + cameraType +
                ", shutterValue=" + shutterValue +
                '}';
    }

    public PayloadIndex getPayloadIndex() {
        return payloadIndex;
    }

    public DrcCameraShutterSetRequest setPayloadIndex(PayloadIndex payloadIndex) {
        this.payloadIndex = payloadIndex;
        return this;
    }

    public ExposureCameraTypeEnum getCameraType() {
        return cameraType;
    }

    public DrcCameraShutterSetRequest setCameraType(ExposureCameraTypeEnum cameraType) {
        this.cameraType = cameraType;
        return this;
    }

    public ShutterSpeedEnum getShutterValue() {
        return shutterValue;
    }

    public DrcCameraShutterSetRequest setShutterValue(ShutterSpeedEnum shutterValue) {
        this.shutterValue = shutterValue;
        return this;
    }
}
