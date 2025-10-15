package com.dji.sdk.cloudapi.wayline;

/**
 * @author Qfei
 * @date 2025/8/16 16:38
 */
public class FlightTaskProgressGetRequest {

    private String sn;

    public FlightTaskProgressGetRequest() {
    }

    @Override
    public String toString() {
        return "FlightTaskProgressGetRequest{" +
                "sn='" + sn + '\'' +
                '}';
    }

    public String getSn() {
        return sn;
    }

    public FlightTaskProgressGetRequest setSn(String sn) {
        this.sn = sn;
        return this;
    }
}
