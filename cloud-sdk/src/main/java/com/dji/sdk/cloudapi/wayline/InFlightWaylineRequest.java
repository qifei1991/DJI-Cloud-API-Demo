package com.dji.sdk.cloudapi.wayline;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Qfei
 * @date 2025/8/16 17:09
 */
public class InFlightWaylineRequest {

    @NotNull
    @Pattern(regexp = "^[^<>:\"/|?*._\\\\]+$")
    private String inFlightWaylineId;

    public InFlightWaylineRequest() {
    }

    @Override
    public String toString() {
        return "InFlightWaylineRequest{" +
                "inFlightWaylineId='" + inFlightWaylineId + '\'' +
                '}';
    }

    public String getInFlightWaylineId() {
        return inFlightWaylineId;
    }

    public InFlightWaylineRequest setInFlightWaylineId(String inFlightWaylineId) {
        this.inFlightWaylineId = inFlightWaylineId;
        return this;
    }
}
