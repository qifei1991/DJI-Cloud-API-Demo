package com.dji.sdk.cloudapi.control;

import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.common.BaseModel;

import javax.validation.constraints.NotNull;

/**
 * 红外照片格式设置
 *
 * @author Qfei
 * @date 2026/3/6 13:59
 */
public class DrcCameraPhotoFormatSetRequest extends BaseModel {

    /**
     * Camera enumeration.
     * It is unofficial device_mode_key.
     * The format is *{type-subtype-gimbalindex}*.
     * Please read [Product Supported](https://developer.dji.com/doc/cloud-api-tutorial/en/overview/product-support.html)
     */
    @NotNull
    private PayloadIndex payloadIndex;

    @NotNull
    private IrPhotoFormatEnum photoFormat;

    public DrcCameraPhotoFormatSetRequest() {
    }

    @Override
    public String toString() {
        return "DrcCameraPhotoFormatSetRequest{" +
                "payloadIndex=" + payloadIndex +
                ", photoFormat=" + photoFormat +
                '}';
    }

    public PayloadIndex getPayloadIndex() {
        return payloadIndex;
    }

    public DrcCameraPhotoFormatSetRequest setPayloadIndex(PayloadIndex payloadIndex) {
        this.payloadIndex = payloadIndex;
        return this;
    }

    public IrPhotoFormatEnum getPhotoFormat() {
        return photoFormat;
    }

    public DrcCameraPhotoFormatSetRequest setPhotoFormat(IrPhotoFormatEnum photoFormat) {
        this.photoFormat = photoFormat;
        return this;
    }
}
