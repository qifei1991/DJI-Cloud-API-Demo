package com.dji.sdk.cloudapi.control;

import com.dji.sdk.cloudapi.device.CameraIsoEnum;
import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.common.BaseModel;

import javax.validation.constraints.NotNull;

/**
 * ISO设置
 *
 * @author Qfei
 * @date 2026/3/5 15:33
 */
public class DrcCameraIsoSetRequest extends BaseModel {

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
    private CameraIsoEnum isoValue;

    public DrcCameraIsoSetRequest() {
    }

    @Override
    public String toString() {
        return "DrcCameraIsoSetRequest{" +
                "payloadIndex=" + payloadIndex +
                ", cameraType=" + cameraType +
                ", isoValue=" + isoValue +
                '}';
    }

    public PayloadIndex getPayloadIndex() {
        return payloadIndex;
    }

    public DrcCameraIsoSetRequest setPayloadIndex(PayloadIndex payloadIndex) {
        this.payloadIndex = payloadIndex;
        return this;
    }

    public ExposureCameraTypeEnum getCameraType() {
        return cameraType;
    }

    public DrcCameraIsoSetRequest setCameraType(ExposureCameraTypeEnum cameraType) {
        this.cameraType = cameraType;
        return this;
    }

    public CameraIsoEnum getIsoValue() {
        return isoValue;
    }

    public DrcCameraIsoSetRequest setIsoValue(CameraIsoEnum isoValue) {
        this.isoValue = isoValue;
        return this;
    }
}
