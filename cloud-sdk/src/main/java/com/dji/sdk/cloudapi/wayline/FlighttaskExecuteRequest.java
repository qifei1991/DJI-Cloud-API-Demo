package com.dji.sdk.cloudapi.wayline;

import com.dji.sdk.common.BaseModel;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author sean
 * @version 1.1
 * @date 2022/6/1
 */
public class FlighttaskExecuteRequest extends BaseModel {

    @NotNull
    @Pattern(regexp = "^[^<>:\"/|?*._\\\\]+$")
    private String flightId;

    /**
     * 用于蛙跳任务，普通航线任务无需下发此参数，只下发flight_id即可
     */
    private FlightTaskMultiDockTask multiDockTask;

    public FlighttaskExecuteRequest() {
    }

    @Override
    public String toString() {
        return "FlighttaskExecuteRequest{" +
                "flightId='" + flightId + '\'' +
                ", multiDockTask=" + multiDockTask +
                '}';
    }

    public String getFlightId() {
        return flightId;
    }

    public FlighttaskExecuteRequest setFlightId(String flightId) {
        this.flightId = flightId;
        return this;
    }

    public FlightTaskMultiDockTask getMultiDockTask() {
        return multiDockTask;
    }

    public FlighttaskExecuteRequest setMultiDockTask(FlightTaskMultiDockTask multiDockTask) {
        this.multiDockTask = multiDockTask;
        return this;
    }
}
