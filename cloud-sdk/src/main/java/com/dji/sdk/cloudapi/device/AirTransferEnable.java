package com.dji.sdk.cloudapi.device;

import com.dji.sdk.common.BaseModel;

/**
 * @author Qfei
 * @date 2024/5/22 17:34
 */
public class AirTransferEnable extends BaseModel {
    private boolean airTransferEnable;

    @Override
    public String toString() {
        return "AirTransferEnable{" +
                "airTransferEnable=" + airTransferEnable +
                '}';
    }

    public boolean isAirTransferEnable() {
        return airTransferEnable;
    }

    public AirTransferEnable setAirTransferEnable(boolean airTransferEnable) {
        this.airTransferEnable = airTransferEnable;
        return this;
    }
}
