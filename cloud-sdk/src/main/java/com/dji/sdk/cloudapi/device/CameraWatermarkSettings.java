package com.dji.sdk.cloudapi.device;

/**
 * @author Qfei
 * @date 2024/5/22 19:19
 */
public class CameraWatermarkSettings {
    private Integer datetimeEnable;
    private Integer droneSnEnable;
    private Integer droneTypeEnable;
    private Integer globalEnable;
    private Integer gpsEnable;
    private Integer layout;
    private String userCustomString;
    private Integer userCustomStringEnable;

    @Override
    public String toString() {
        return "CameraWatermarkSettings{" +
                "datetimeEnable=" + datetimeEnable +
                ", droneSnEnable=" + droneSnEnable +
                ", droneTypeEnable=" + droneTypeEnable +
                ", globalEnable=" + globalEnable +
                ", gpsEnable=" + gpsEnable +
                ", layout=" + layout +
                ", userCustomString='" + userCustomString + '\'' +
                ", userCustomStringEnable=" + userCustomStringEnable +
                '}';
    }

    public Integer getDatetimeEnable() {
        return datetimeEnable;
    }

    public CameraWatermarkSettings setDatetimeEnable(Integer datetimeEnable) {
        this.datetimeEnable = datetimeEnable;
        return this;
    }

    public Integer getDroneSnEnable() {
        return droneSnEnable;
    }

    public CameraWatermarkSettings setDroneSnEnable(Integer droneSnEnable) {
        this.droneSnEnable = droneSnEnable;
        return this;
    }

    public Integer getDroneTypeEnable() {
        return droneTypeEnable;
    }

    public CameraWatermarkSettings setDroneTypeEnable(Integer droneTypeEnable) {
        this.droneTypeEnable = droneTypeEnable;
        return this;
    }

    public Integer getGlobalEnable() {
        return globalEnable;
    }

    public CameraWatermarkSettings setGlobalEnable(Integer globalEnable) {
        this.globalEnable = globalEnable;
        return this;
    }

    public Integer getGpsEnable() {
        return gpsEnable;
    }

    public CameraWatermarkSettings setGpsEnable(Integer gpsEnable) {
        this.gpsEnable = gpsEnable;
        return this;
    }

    public Integer getLayout() {
        return layout;
    }

    public CameraWatermarkSettings setLayout(Integer layout) {
        this.layout = layout;
        return this;
    }

    public String getUserCustomString() {
        return userCustomString;
    }

    public CameraWatermarkSettings setUserCustomString(String userCustomString) {
        this.userCustomString = userCustomString;
        return this;
    }

    public Integer getUserCustomStringEnable() {
        return userCustomStringEnable;
    }

    public CameraWatermarkSettings setUserCustomStringEnable(Integer userCustomStringEnable) {
        this.userCustomStringEnable = userCustomStringEnable;
        return this;
    }
}
