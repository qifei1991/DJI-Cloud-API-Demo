package com.dji.sdk.cloudapi.device;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MobilityStatusNotify
 *
 * @author Qfei
 * @date 2025/4/22 12:53
 */
public class MobilityStatusNotify {

    @JsonProperty("mobility_status_notify")
    private Integer mobilityStatusNotify;

    @Override
    public String toString() {
        return "MobilityStatusNotify{" +
                "mobilityStatusNotify=" + mobilityStatusNotify +
                '}';
    }

    public Integer getMobilityStatusNotify() {
        return mobilityStatusNotify;
    }

    public MobilityStatusNotify setMobilityStatusNotify(Integer mobilityStatusNotify) {
        this.mobilityStatusNotify = mobilityStatusNotify;
        return this;
    }
}
