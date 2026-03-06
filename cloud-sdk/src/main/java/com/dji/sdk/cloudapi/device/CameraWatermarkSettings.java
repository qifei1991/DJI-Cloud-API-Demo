package com.dji.sdk.cloudapi.device;

/**
 * @author Qfei
 * @date 2024/5/22 19:19
 */
public class CameraWatermarkSettings {

    private SwitchActionEnum globalEnable;

    private SwitchActionEnum droneTypeEnable;

    private SwitchActionEnum droneSnEnable;

    private SwitchActionEnum datetimeEnable;

    private SwitchActionEnum gpsEnable;

    private SwitchActionEnum userCustomStringEnable;

    private String userCustomString;

    private WatermarkLayoutEnum layout;

    public CameraWatermarkSettings() {
    }

    @Override
    public String toString() {
        return "CameraWatermarkSettings{" +
                "globalEnable=" + globalEnable +
                ", droneTypeEnable=" + droneTypeEnable +
                ", droneSnEnable=" + droneSnEnable +
                ", datetimeEnable=" + datetimeEnable +
                ", gpsEnable=" + gpsEnable +
                ", userCustomStringEnable=" + userCustomStringEnable +
                ", userCustomString='" + userCustomString + '\'' +
                ", layout=" + layout +
                '}';
    }

    public SwitchActionEnum getGlobalEnable() {
        return globalEnable;
    }

    public CameraWatermarkSettings setGlobalEnable(SwitchActionEnum globalEnable) {
        this.globalEnable = globalEnable;
        return this;
    }

    public SwitchActionEnum getDroneTypeEnable() {
        return droneTypeEnable;
    }

    public CameraWatermarkSettings setDroneTypeEnable(SwitchActionEnum droneTypeEnable) {
        this.droneTypeEnable = droneTypeEnable;
        return this;
    }

    public SwitchActionEnum getDroneSnEnable() {
        return droneSnEnable;
    }

    public CameraWatermarkSettings setDroneSnEnable(SwitchActionEnum droneSnEnable) {
        this.droneSnEnable = droneSnEnable;
        return this;
    }

    public SwitchActionEnum getDatetimeEnable() {
        return datetimeEnable;
    }

    public CameraWatermarkSettings setDatetimeEnable(SwitchActionEnum datetimeEnable) {
        this.datetimeEnable = datetimeEnable;
        return this;
    }

    public SwitchActionEnum getGpsEnable() {
        return gpsEnable;
    }

    public CameraWatermarkSettings setGpsEnable(SwitchActionEnum gpsEnable) {
        this.gpsEnable = gpsEnable;
        return this;
    }

    public SwitchActionEnum getUserCustomStringEnable() {
        return userCustomStringEnable;
    }

    public CameraWatermarkSettings setUserCustomStringEnable(SwitchActionEnum userCustomStringEnable) {
        this.userCustomStringEnable = userCustomStringEnable;
        return this;
    }

    public String getUserCustomString() {
        return userCustomString;
    }

    public CameraWatermarkSettings setUserCustomString(String userCustomString) {
        this.userCustomString = userCustomString;
        return this;
    }

    public WatermarkLayoutEnum getLayout() {
        return layout;
    }

    public CameraWatermarkSettings setLayout(WatermarkLayoutEnum layout) {
        this.layout = layout;
        return this;
    }
}
