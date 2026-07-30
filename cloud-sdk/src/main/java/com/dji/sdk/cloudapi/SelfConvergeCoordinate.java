package com.dji.sdk.cloudapi;

/**
 * 自收敛坐标
 *
 * @author Qfei
 * @date 2026/7/24 16:05
 */
public class SelfConvergeCoordinate {

    private Float latitude;

    private Float longitude;

    private Float altitude;

    public SelfConvergeCoordinate() {
    }

    @Override
    public String toString() {
        return "SelfConvergeCoordinate{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                '}';
    }

    public Float getLatitude() {
        return latitude;
    }

    public SelfConvergeCoordinate setLatitude(Float latitude) {
        this.latitude = latitude;
        return this;
    }

    public Float getLongitude() {
        return longitude;
    }

    public SelfConvergeCoordinate setLongitude(Float longitude) {
        this.longitude = longitude;
        return this;
    }

    public Float getAltitude() {
        return altitude;
    }

    public SelfConvergeCoordinate setAltitude(Float altitude) {
        this.altitude = altitude;
        return this;
    }
}
