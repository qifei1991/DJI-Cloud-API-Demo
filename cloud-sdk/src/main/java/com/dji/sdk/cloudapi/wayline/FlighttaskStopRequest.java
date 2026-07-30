package com.dji.sdk.cloudapi.wayline;

/**
 * 飞行计划结束请求参数
 *
 * @author Qfei
 * @date 2026/7/27 16:36
 */
public class FlighttaskStopRequest {

    private String flightId;

    private FlighttaskStopReasonEnum reason;

    public FlighttaskStopRequest() {
    }

    @Override
    public String toString() {
        return "FlighttaskStopRequest{" +
                "flightId='" + flightId + '\'' +
                ", reason=" + reason +
                '}';
    }

    public String getFlightId() {
        return flightId;
    }

    public FlighttaskStopRequest setFlightId(String flightId) {
        this.flightId = flightId;
        return this;
    }

    public FlighttaskStopReasonEnum getReason() {
        return reason;
    }

    public FlighttaskStopRequest setReason(FlighttaskStopReasonEnum reason) {
        this.reason = reason;
        return this;
    }
}
