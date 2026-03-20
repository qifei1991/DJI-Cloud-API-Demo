package com.dji.sdk.cloudapi.control;

import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.common.BaseModel;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 框选变焦
 *
 * @author Qfei
 * @date 2026/3/13 17:42
 */
public class CameraFrameZoomRequest extends BaseModel {

    /**
     * Camera enumeration.
     * It is unofficial device_mode_key.
     * The format is *{type-subtype-gimbalindex}*.
     * Please read [Product Supported](https://developer.dji.com/doc/cloud-api-tutorial/en/overview/product-support.html)
     */
    @NotNull
    private PayloadIndex payloadIndex;

    @NotNull
    private CameraTypeEnum cameraType;

    /**
     * true: Lock the gimbal, the gimbal and the drone rotate together.
     * false: Only the gimbal rotates, but the drone does not.
     */
    @NotNull
    private Boolean locked;

    /**
     * upper left corner as center point. {"max":"1","min":"0","step":"0.000001","unit_name":"无 / "}
     */
    @Min(0)
    @Max(1)
    private Float x;

    @Min(0)
    @Max(1)
    private Float y;

    /**
     * 目标框宽度 {"max":"1","min":"0","step":"0.000001","unit_name":"无 / "}
     */
    @NotNull
    @Min(0)
    @Max(1)
    private Float width;

    /**
     * 目标框高度 {"max":"1","min":"0","step":"0.000001","unit_name":"无 / "}
     */
    @NotNull
    @Min(0)
    @Max(1)
    private Float height;

    public CameraFrameZoomRequest() {
    }

    @Override
    public String toString() {
        return "CameraFrameZoomRequest{" +
                "payloadIndex=" + payloadIndex +
                ", cameraType=" + cameraType +
                ", locked=" + locked +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    public PayloadIndex getPayloadIndex() {
        return payloadIndex;
    }

    public CameraFrameZoomRequest setPayloadIndex(PayloadIndex payloadIndex) {
        this.payloadIndex = payloadIndex;
        return this;
    }

    public CameraTypeEnum getCameraType() {
        return cameraType;
    }

    public CameraFrameZoomRequest setCameraType(CameraTypeEnum cameraType) {
        this.cameraType = cameraType;
        return this;
    }

    public Boolean getLocked() {
        return locked;
    }

    public CameraFrameZoomRequest setLocked(Boolean locked) {
        this.locked = locked;
        return this;
    }

    public Float getX() {
        return x;
    }

    public CameraFrameZoomRequest setX(Float x) {
        this.x = x;
        return this;
    }

    public Float getY() {
        return y;
    }

    public CameraFrameZoomRequest setY(Float y) {
        this.y = y;
        return this;
    }

    public Float getWidth() {
        return width;
    }

    public CameraFrameZoomRequest setWidth(Float width) {
        this.width = width;
        return this;
    }

    public Float getHeight() {
        return height;
    }

    public CameraFrameZoomRequest setHeight(Float height) {
        this.height = height;
        return this;
    }
}
