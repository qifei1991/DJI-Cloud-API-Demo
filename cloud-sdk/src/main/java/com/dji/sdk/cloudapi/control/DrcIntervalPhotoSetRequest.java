package com.dji.sdk.cloudapi.control;

import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.common.BaseModel;

import javax.validation.constraints.NotNull;

/**
 * 定时拍照
 * <pre>不同照片尺寸取值范围不一样。照片尺寸在 8K 以下时拍照间隔的设置没有限制。8K 照片不能设置0.7s 和 1s。请忽略照片尺寸在 8K 以上的场景</pre>
 *
 * @author Qfei
 * @date 2026/3/6 17:11
 */
public class DrcIntervalPhotoSetRequest extends BaseModel {

    /**
     * Camera enumeration.
     * It is unofficial device_mode_key.
     * The format is *{type-subtype-gimbalindex}*.
     * Please read [Product Supported](https://developer.dji.com/doc/cloud-api-tutorial/en/overview/product-support.html)
     */
    @NotNull
    private PayloadIndex payloadIndex;

    @NotNull
    private PhotoIntervalEnum interval;

    public DrcIntervalPhotoSetRequest() {
    }

    @Override
    public String toString() {
        return "DrcIntervalPhotoSetRequest{" +
                "payloadIndex=" + payloadIndex +
                ", interval=" + interval +
                '}';
    }

    public PayloadIndex getPayloadIndex() {
        return payloadIndex;
    }

    public DrcIntervalPhotoSetRequest setPayloadIndex(PayloadIndex payloadIndex) {
        this.payloadIndex = payloadIndex;
        return this;
    }

    public PhotoIntervalEnum getInterval() {
        return interval;
    }

    public DrcIntervalPhotoSetRequest setInterval(PhotoIntervalEnum interval) {
        this.interval = interval;
        return this;
    }
}
