package com.dji.sdk.cloudapi.device;

import com.dji.sdk.common.BaseModel;

/**
 * @author Qfei
 * @date 2024/8/13 18:52
 */
public class GeoCagingStatusInfo extends BaseModel {

    private GeoCagingStatus geoCagingStatus;

    @Override
    public String toString() {
        return "GeoCagingStatusInfo{" +
                "geoCagingStatus=" + geoCagingStatus +
                '}';
    }

    public GeoCagingStatus getGeoCagingStatus() {
        return geoCagingStatus;
    }

    public GeoCagingStatusInfo setGeoCagingStatus(GeoCagingStatus geoCagingStatus) {
        this.geoCagingStatus = geoCagingStatus;
        return this;
    }
}
