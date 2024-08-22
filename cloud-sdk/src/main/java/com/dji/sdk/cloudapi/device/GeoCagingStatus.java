package com.dji.sdk.cloudapi.device;

/**
 * @author Qfei
 * @date 2024/8/13 19:00
 */
public class GeoCagingStatus {

    private Integer state;

    @Override
    public String toString() {
        return "GeoCagingStatus{" +
                "state=" + state +
                '}';
    }

    public Integer getState() {
        return state;
    }

    public GeoCagingStatus setState(Integer state) {
        this.state = state;
        return this;
    }
}
