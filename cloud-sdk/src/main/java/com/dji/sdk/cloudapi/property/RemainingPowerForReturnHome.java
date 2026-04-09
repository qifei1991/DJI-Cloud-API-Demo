package com.dji.sdk.cloudapi.property;

import com.dji.sdk.common.BaseModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 返航电量设置
 *
 * @author Qfei
 * @date 2026/4/6 11:51
 */
public class RemainingPowerForReturnHome extends BaseModel {

    @NotNull
    @Min(0)
    @Max(100)
    @JsonProperty("remaining_power_for_return_home")
    private Integer remainingPowerForReturnHome;

    public RemainingPowerForReturnHome() {
    }

    @Override
    public String toString() {
        return "RemainingPowerForReturnHome{" +
                "remainingPowerForReturnHome=" + remainingPowerForReturnHome +
                '}';
    }

    public RemainingPowerForReturnHome setRemainingPowerForReturnHome(Integer remainingPowerForReturnHome) {
        this.remainingPowerForReturnHome = remainingPowerForReturnHome;
        return this;
    }

    public Integer getRemainingPowerForReturnHome() {
        return remainingPowerForReturnHome;
    }
}
