package com.dji.sdk.cloudapi.device;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Qfei
 * @date 2026/1/14 18:54
 */
public class BeidouVersion {

    @JsonProperty("is_beidou_version")
    private Boolean beidouVersion;

    @Override
    public String toString() {
        return "BeidouVersion{" +
                "beidouVersion=" + beidouVersion +
                '}';
    }

    public Boolean getBeidouVersion() {
        return beidouVersion;
    }

    public BeidouVersion setBeidouVersion(Boolean beidouVersion) {
        this.beidouVersion = beidouVersion;
        return this;
    }
}
