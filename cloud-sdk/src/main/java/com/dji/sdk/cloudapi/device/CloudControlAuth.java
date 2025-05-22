package com.dji.sdk.cloudapi.device;

/**
 * @author Qfei
 * @date 2025/5/21 14:59
 */
public class CloudControlAuth {

    private Boolean support;

    public CloudControlAuth() {
    }

    @Override
    public String toString() {
        return "CloudControlAuth{" +
                "support=" + support +
                '}';
    }

    public Boolean getSupport() {
        return support;
    }

    public CloudControlAuth setSupport(Boolean support) {
        this.support = support;
        return this;
    }
}
