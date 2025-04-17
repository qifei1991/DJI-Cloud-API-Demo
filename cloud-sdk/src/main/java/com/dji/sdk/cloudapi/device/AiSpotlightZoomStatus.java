package com.dji.sdk.cloudapi.device;

import com.dji.sdk.common.BaseModel;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Qfei
 * @date 2025/4/11 15:08
 */
public class AiSpotlightZoomStatus extends BaseModel {

    @JsonProperty("ai_spotlight_zoom_state")
    private AiSpotlightZoomState aiSpotlightZoomState;

    @Override
    public String toString() {
        return "AiSpotlightZoomStatus{" +
                "aiSpotlightZoomState=" + aiSpotlightZoomState +
                '}';
    }

    public AiSpotlightZoomState getAiSpotlightZoomState() {
        return aiSpotlightZoomState;
    }

    public AiSpotlightZoomStatus setAiSpotlightZoomState(AiSpotlightZoomState aiSpotlightZoomState) {
        this.aiSpotlightZoomState = aiSpotlightZoomState;
        return this;
    }
}
