package com.dji.sdk.config.version;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * O4 地面站（device domain 8，type 135）的物模型版本与 CloudSDK 版本的对应关系。
 * <p>
 * O4 地面站自 上云 API v1.18 起支持（漫游基站模式），自 v1.20 起可作为网关接入
 * 大疆机场 4（9-131-0）、中继、飞行器等子设备，因此统一按 上云 API v1.20 语义处理。
 * <p>
 * 目前官方尚未按固件版本细分 O4 地面站的能力差异，故 thing_version 不做精确匹配，
 * 后续需要区分时在此补充版本项并改为精确匹配。
 *
 * @version 1.20
 * @date 2026/11/20
 */
public enum O4GroundStationThingVersionEnum implements IThingVersion {

    V1_0_0("1.0.0", CloudSDKVersionEnum.V1_20_0),
    ;

    private final String thingVersion;

    private final CloudSDKVersionEnum cloudSDKVersion;

    O4GroundStationThingVersionEnum(String thingVersion, CloudSDKVersionEnum cloudSDKVersion) {
        this.thingVersion = thingVersion;
        this.cloudSDKVersion = cloudSDKVersion;
    }

    @JsonValue
    @Override
    public String getThingVersion() {
        return thingVersion;
    }

    @Override
    public CloudSDKVersionEnum getCloudSDKVersion() {
        return cloudSDKVersion;
    }

    /**
     * O4 地面站的 thing_version 暂不细分，任何上报值都按最新版本语义处理，避免新固件上线即注册失败。
     * @param thingVersion 设备上报的物模型版本
     * @return 对应的 O4 地面站版本项
     */
    public static O4GroundStationThingVersionEnum find(String thingVersion) {
        return V1_0_0;
    }
}