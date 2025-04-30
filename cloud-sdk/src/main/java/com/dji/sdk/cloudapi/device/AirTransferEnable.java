package com.dji.sdk.cloudapi.device;

import com.dji.sdk.common.BaseModel;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Qfei
 * @date 2024/5/22 17:34
 */
public class AirTransferEnable extends BaseModel {

    @JsonProperty("air_transfer_enable")
    private Boolean airTransferEnable;

    @Override
    public String toString() {
        return "AirTransferEnable{" +
                "airTransferEnable=" + airTransferEnable +
                '}';
    }

    public Boolean isAirTransferEnable() {
        return airTransferEnable;
    }

    public AirTransferEnable setAirTransferEnable(Boolean airTransferEnable) {
        this.airTransferEnable = airTransferEnable;
        return this;
    }
}
