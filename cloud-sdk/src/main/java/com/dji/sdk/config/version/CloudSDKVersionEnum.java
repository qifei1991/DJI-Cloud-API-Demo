package com.dji.sdk.config.version;

/**
 * @author sean
 * @version 1.7
 * @date 2023/5/22
 */
public enum CloudSDKVersionEnum {

    V0_0_1("0.0.1"),

    V1_0_0("1.0.0"),

    V1_0_1("1.0.1"),

    V1_0_2("1.0.2"),

    V1_0_3("1.0.3"),

    /**
     * 上云 API v1.20：O4 地面站（8-135-0）作为网关接入，大疆机场 4（9-131-0）等作为子设备上云。
     */
    V1_20_0("1.20.0"),

    DEFAULT("1.20.0"),

    V99("99");

    private final String version;

    CloudSDKVersionEnum(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public boolean isSupported(CloudSDKVersionEnum version) {
        return this.version.compareTo(version.getVersion()) >= 0;
    }

    public boolean isDeprecated(CloudSDKVersionEnum version) {
        return this.version.compareTo(version.getVersion()) >= 0;
    }
}
