package com.dji.sdk.cloudapi.device;

import com.dji.sdk.common.BaseModel;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Qfei
 * @date 2025/4/11 14:58
 */
public class AiSpotlightZoomOn extends BaseModel {

    @JsonProperty("ai_spotlight_zoom_on")
    private Integer aiSpotlightZoomOn;

    @Override
    public String toString() {
        return "AiSpotlightZoomOn{" +
                "aiSpotlightZoomOn=" + aiSpotlightZoomOn +
                '}';
    }

    public Integer getAiSpotlightZoomOn() {
        return aiSpotlightZoomOn;
    }

    public AiSpotlightZoomOn setAiSpotlightZoomOn(Integer aiSpotlightZoomOn) {
        this.aiSpotlightZoomOn = aiSpotlightZoomOn;
        return this;
    }
}
