package com.dji.sdk.cloudapi.device;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Qfei
 * @date 2025/5/21 14:56
 */
public class RcCapabilitySet {

    @JsonProperty("capability_set")
    private CapabilitySet capabilitySet;

    public RcCapabilitySet() {
    }

    @Override
    public String toString() {
        return "RcCapabilitySet{" +
                "capabilitySet=" + capabilitySet +
                '}';
    }

    public CapabilitySet getCapabilitySet() {
        return capabilitySet;
    }

    public RcCapabilitySet setCapabilitySet(CapabilitySet capabilitySet) {
        this.capabilitySet = capabilitySet;
        return this;
    }
}
