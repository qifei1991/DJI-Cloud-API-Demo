package com.dji.sdk.cloudapi.device;

import com.dji.sdk.common.BaseModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Dock3新加的状态
 *
 * @author Qfei
 * @date 2025/3/27 14:08
 */
public class DepartureTrajectory extends BaseModel {

    @JsonProperty("departure_trajectory")
    private List<Object> departureTrajectory;

    @Override
    public String toString() {
        return "DepartureTrajectory{" +
                "departureTrajectory=" + departureTrajectory +
                '}';
    }

    public List<Object> getDepartureTrajectory() {
        return departureTrajectory;
    }

    public DepartureTrajectory setDepartureTrajectory(List<Object> departureTrajectory) {
        this.departureTrajectory = departureTrajectory;
        return this;
    }
}
