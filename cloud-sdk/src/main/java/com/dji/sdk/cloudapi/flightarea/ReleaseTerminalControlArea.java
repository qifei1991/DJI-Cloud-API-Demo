package com.dji.sdk.cloudapi.flightarea;

/**
 * @author Qfei
 * @date 2026/7/23 15:25
 */
public class ReleaseTerminalControlArea {

    private String flightId;

    private Integer reason;

    public ReleaseTerminalControlArea() {
    }

    @Override
    public String toString() {
        return "ReleaseTerminalControlArea{" +
                "flightId='" + flightId + '\'' +
                ", reason=" + reason +
                '}';
    }

    public String getFlightId() {
        return flightId;
    }

    public ReleaseTerminalControlArea setFlightId(String flightId) {
        this.flightId = flightId;
        return this;
    }

    public Integer getReason() {
        return reason;
    }

    public ReleaseTerminalControlArea setReason(Integer reason) {
        this.reason = reason;
        return this;
    }
}
