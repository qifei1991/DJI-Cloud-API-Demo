package com.dji.sdk.cloudapi.device;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Qfei
 * @date 2025/5/21 14:58
 */
public class CapabilitySet {

    @JsonProperty("cloud_control_auth")
    private CloudControlAuth cloudControlAuth;

    public CapabilitySet() {
    }

    @Override
    public String toString() {
        return "CapabilitySet{" +
                "cloudControlAuth=" + cloudControlAuth +
                '}';
    }

    public CloudControlAuth getCloudControlAuth() {
        return cloudControlAuth;
    }

    public CapabilitySet setCloudControlAuth(CloudControlAuth cloudControlAuth) {
        this.cloudControlAuth = cloudControlAuth;
        return this;
    }
}
