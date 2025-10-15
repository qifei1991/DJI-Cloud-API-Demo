package com.dji.sdk.cloudapi.wayline;

/**
 * @author Qfei
 * @date 2025/8/18 17:29
 */
public class InFlightWaylineProgressData {

    /**
     * Progress value
     */
    private Integer percent;

    public InFlightWaylineProgressData() {
    }

    @Override
    public String toString() {
        return "InFlightWaylineProgressData{" +
                "percent=" + percent +
                '}';
    }

    public Integer getPercent() {
        return percent;
    }

    public InFlightWaylineProgressData setPercent(Integer percent) {
        this.percent = percent;
        return this;
    }
}
