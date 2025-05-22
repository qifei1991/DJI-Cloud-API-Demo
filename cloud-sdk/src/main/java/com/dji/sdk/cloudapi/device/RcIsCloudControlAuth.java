package com.dji.sdk.cloudapi.device;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Qfei
 * @date 2025/5/21 15:03
 */
public class RcIsCloudControlAuth {

    @JsonProperty("is_cloud_control_auth")
    private Boolean cloudControlAuth;

    public RcIsCloudControlAuth() {
    }

    @Override
    public String toString() {
        return "RcIsCloudControlAuth{" +
                "cloudControlAuth=" + cloudControlAuth +
                '}';
    }

    public Boolean getCloudControlAuth() {
        return cloudControlAuth;
    }

    public RcIsCloudControlAuth setCloudControlAuth(Boolean cloudControlAuth) {
        this.cloudControlAuth = cloudControlAuth;
        return this;
    }
}
