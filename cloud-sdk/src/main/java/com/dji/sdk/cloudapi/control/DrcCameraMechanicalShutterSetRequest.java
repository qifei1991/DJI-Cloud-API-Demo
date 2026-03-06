package com.dji.sdk.cloudapi.control;

import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.common.BaseModel;

import javax.validation.constraints.NotNull;

/**
 * 机械快门设置
 *
 * @author Qfei
 * @date 2026/3/5 16:23
 */
public class DrcCameraMechanicalShutterSetRequest extends BaseModel {

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
    private MechanicalShutterStateEnum mechanicalShutterState;

    public DrcCameraMechanicalShutterSetRequest() {
    }

    @Override
    public String toString() {
        return "DrcCameraMechanicalShutterSetRequest{" +
                "payloadIndex=" + payloadIndex +
                ", cameraType=" + cameraType +
                ", mechanicalShutterState=" + mechanicalShutterState +
                '}';
    }

    public PayloadIndex getPayloadIndex() {
        return payloadIndex;
    }

    public DrcCameraMechanicalShutterSetRequest setPayloadIndex(PayloadIndex payloadIndex) {
        this.payloadIndex = payloadIndex;
        return this;
    }

    public ExposureCameraTypeEnum getCameraType() {
        return cameraType;
    }

    public DrcCameraMechanicalShutterSetRequest setCameraType(ExposureCameraTypeEnum cameraType) {
        this.cameraType = cameraType;
        return this;
    }

    public MechanicalShutterStateEnum getMechanicalShutterState() {
        return mechanicalShutterState;
    }

    public DrcCameraMechanicalShutterSetRequest setMechanicalShutterState(MechanicalShutterStateEnum mechanicalShutterState) {
        this.mechanicalShutterState = mechanicalShutterState;
        return this;
    }
}
