package com.dji.sdk.cloudapi.device;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AiIdentifyOn
 *
 * @author Qfei
 * @date 2025/4/22 13:00
 */
public class AiIdentifyOn {

    @JsonProperty("ai_identify_on")
    private Integer aiIdentifyOn;

    public AiIdentifyOn() {
    }

    @Override
    public String toString() {
        return "AiIdentifyOn{" +
                "aiIdentifyOn=" + aiIdentifyOn +
                '}';
    }

    public Integer getAiIdentifyOn() {
        return aiIdentifyOn;
    }

    public AiIdentifyOn setAiIdentifyOn(Integer aiIdentifyOn) {
        this.aiIdentifyOn = aiIdentifyOn;
        return this;
    }
}
