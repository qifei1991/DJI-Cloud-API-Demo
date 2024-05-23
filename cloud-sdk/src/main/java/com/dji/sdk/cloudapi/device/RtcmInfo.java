package com.dji.sdk.cloudapi.device;

import com.dji.sdk.common.BaseModel;

/**
 * @author Qfei
 * @date 2024/5/22 17:16
 */
public class RtcmInfo extends BaseModel {
    private Rtcm rtcmInfo;

    @Override
    public String toString() {
        return "RtcmInfo{" +
                "rtcmInfo=" + rtcmInfo +
                '}';
    }

    public Rtcm getRtcmInfo() {
        return rtcmInfo;
    }

    public RtcmInfo setRtcmInfo(Rtcm rtcmInfo) {
        this.rtcmInfo = rtcmInfo;
        return this;
    }
}
