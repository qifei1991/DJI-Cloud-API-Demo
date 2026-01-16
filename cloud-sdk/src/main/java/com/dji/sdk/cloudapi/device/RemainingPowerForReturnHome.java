package com.dji.sdk.cloudapi.device;

/**
 * @author Qfei
 * @date 2026/1/14 18:50
 */
public class RemainingPowerForReturnHome {

    private Integer remainingPowerForReturnHome;

    @Override
    public String toString() {
        return "RemainingPowerForReturnHome{" +
                "remainingPowerForReturnHome=" + remainingPowerForReturnHome +
                '}';
    }

    public Integer getRemainingPowerForReturnHome() {
        return remainingPowerForReturnHome;
    }

    public RemainingPowerForReturnHome setRemainingPowerForReturnHome(Integer remainingPowerForReturnHome) {
        this.remainingPowerForReturnHome = remainingPowerForReturnHome;
        return this;
    }
}
