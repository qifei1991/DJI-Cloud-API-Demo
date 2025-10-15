package com.dji.sdk.cloudapi.wayline;

import com.dji.sdk.common.BaseModel;

/**
 * @author Qfei
 * @date 2025/8/16 16:41
 */
public class FlightTaskProgressGetResponse extends BaseModel {

    private String flightId;

    private FlighttaskProgressData progress;

    private FlighttaskStatusEnum status;

    public FlightTaskProgressGetResponse() {
    }

    @Override
    public String toString() {
        return "FlightTaskProgressGetResponse{" +
                "flightId='" + flightId + '\'' +
                ", progress=" + progress +
                ", status=" + status +
                '}';
    }

    public String getFlightId() {
        return flightId;
    }

    public FlightTaskProgressGetResponse setFlightId(String flightId) {
        this.flightId = flightId;
        return this;
    }

    public FlighttaskProgressData getProgress() {
        return progress;
    }

    public FlightTaskProgressGetResponse setProgress(FlighttaskProgressData progress) {
        this.progress = progress;
        return this;
    }

    public FlighttaskStatusEnum getStatus() {
        return status;
    }

    public FlightTaskProgressGetResponse setStatus(FlighttaskStatusEnum status) {
        this.status = status;
        return this;
    }
}
