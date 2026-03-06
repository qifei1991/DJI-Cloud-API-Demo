package com.dji.sdk.cloudapi.control;

import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.common.BaseModel;

import javax.validation.constraints.NotNull;

/**
 * 相机光圈设置
 *
 * @author Qfei
 * @date 2026/3/5 16:01
 */
public class DrcCameraApertureValueSetRequest extends BaseModel {

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
    private CameraApertureEnum apertureValue;

    public DrcCameraApertureValueSetRequest() {
    }

    @Override
    public String toString() {
        return "DrcCameraApertureValueSetRequest{" +
                "payloadIndex=" + payloadIndex +
                ", cameraType=" + cameraType +
                ", apertureValue=" + apertureValue +
                '}';
    }

    public PayloadIndex getPayloadIndex() {
        return payloadIndex;
    }

    public DrcCameraApertureValueSetRequest setPayloadIndex(PayloadIndex payloadIndex) {
        this.payloadIndex = payloadIndex;
        return this;
    }

    public ExposureCameraTypeEnum getCameraType() {
        return cameraType;
    }

    public DrcCameraApertureValueSetRequest setCameraType(ExposureCameraTypeEnum cameraType) {
        this.cameraType = cameraType;
        return this;
    }

    public CameraApertureEnum getApertureValue() {
        return apertureValue;
    }

    public DrcCameraApertureValueSetRequest setApertureValue(CameraApertureEnum apertureValue) {
        this.apertureValue = apertureValue;
        return this;
    }
}
