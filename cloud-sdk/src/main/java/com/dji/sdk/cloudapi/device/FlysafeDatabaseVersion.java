package com.dji.sdk.cloudapi.device;

import com.dji.sdk.common.BaseModel;

/**
 * @author Qfei
 * @date 2024/5/22 17:29
 */
public class FlysafeDatabaseVersion extends BaseModel {
    private String flysafeDatabaseVersion;

    @Override
    public String toString() {
        return "FlysafeDatabaseVersion{" +
                "flysafeDatabaseVersion='" + flysafeDatabaseVersion + '\'' +
                '}';
    }

    public String getFlysafeDatabaseVersion() {
        return flysafeDatabaseVersion;
    }

    public FlysafeDatabaseVersion setFlysafeDatabaseVersion(String flysafeDatabaseVersion) {
        this.flysafeDatabaseVersion = flysafeDatabaseVersion;
        return this;
    }
}
