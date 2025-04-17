package com.dji.sdk.cloudapi.device;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 聚光灯状态
 *
 * @author Qfei
 * @date 2025/4/11 15:03
 */
public class AiSpotlightZoomState {

    @JsonProperty("exception_status")
    private Integer exceptionStatus;

    private Integer state;

    @Override
    public String toString() {
        return "AiSpotlightZoomState{" +
                "exceptionStatus=" + exceptionStatus +
                ", state=" + state +
                '}';
    }

    public Integer getExceptionStatus() {
        return exceptionStatus;
    }

    public AiSpotlightZoomState setExceptionStatus(Integer exceptionStatus) {
        this.exceptionStatus = exceptionStatus;
        return this;
    }

    public Integer getState() {
        return state;
    }

    public AiSpotlightZoomState setState(Integer state) {
        this.state = state;
        return this;
    }
}
