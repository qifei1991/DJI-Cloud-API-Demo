package com.dji.sdk.cloudapi.device;

/**
 * @author Qfei
 * @date 2024/5/22 17:19
 */
public class Rtcm {
    private String host;
    private String port;
    private String mountPoint;
    private Integer rtcmDeviceType;
    private Integer sourceType;

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

    public Integer getRtcmDeviceType() {
        return rtcmDeviceType;
    }

    public Rtcm setRtcmDeviceType(Integer rtcmDeviceType) {
        this.rtcmDeviceType = rtcmDeviceType;
        return this;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public Rtcm setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
        return this;
    }
}
