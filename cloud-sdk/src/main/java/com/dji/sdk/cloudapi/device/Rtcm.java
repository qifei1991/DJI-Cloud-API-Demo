package com.dji.sdk.cloudapi.device;

import com.dji.sdk.cloudapi.RtcmSourceTypeEnum;

/**
 * @author Qfei
 * @date 2024/5/22 17:19
 */
public class Rtcm {
    private String host;
    private String port;
    private String mountPoint;
    private RtcmDeviceTypeEnum rtcmDeviceType;
    private RtcmSourceTypeEnum sourceType;

    @Override
    public String toString() {
        return "Rtcm{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", mountPoint='" + mountPoint + '\'' +
                ", rtcmDeviceType=" + rtcmDeviceType +
                ", sourceType=" + sourceType +
                '}';
    }

    public String getHost() {
        return host;
    }

    public Rtcm setHost(String host) {
        this.host = host;
        return this;
    }

    public String getPort() {
        return port;
    }

    public Rtcm setPort(String port) {
        this.port = port;
        return this;
    }

    public String getMountPoint() {
        return mountPoint;
    }

    public Rtcm setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
        return this;
    }

    public RtcmDeviceTypeEnum getRtcmDeviceType() {
        return rtcmDeviceType;
    }

    public Rtcm setRtcmDeviceType(RtcmDeviceTypeEnum rtcmDeviceType) {
        this.rtcmDeviceType = rtcmDeviceType;
        return this;
    }

    public RtcmSourceTypeEnum getSourceType() {
        return sourceType;
    }

    public Rtcm setSourceType(RtcmSourceTypeEnum sourceType) {
        this.sourceType = sourceType;
        return this;
    }
}
