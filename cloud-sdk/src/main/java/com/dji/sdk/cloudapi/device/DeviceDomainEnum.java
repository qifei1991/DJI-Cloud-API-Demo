package com.dji.sdk.cloudapi.device;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 *
 * @author sean.zhou
 * @date 2021/11/15
 * @version 0.1
 */
@Schema(description = "device domain", enumAsRef = true)
public enum DeviceDomainEnum {

    /**
     * 飞机类
     */
    DRONE(0),

    /**
     * 负载类
     */
    PAYLOAD(1),

    /**
     * 遥控器类
     */
    REMOTER_CONTROL(2),

    /**
     * 机场类
     */
    DOCK (3),

    /**
     * 中继类，如 DJI O4 地面站以中继角色接入网关设备时上报（原 D-RTK 3 中继站）
     */
    RELAY(6),

    /**
     * 电源类，如 DJI Power 移动电源，作为子设备接入 DJI O4 地面站上云
     */
    POWER(7),

    /**
     * 基站类（网络基站），如 DJI O4 地面站，搭配大疆机场 3、大疆机场 4
     */
    BASE_STATION(8),

    /**
     * 机场类，机场作为子设备接入网关设备时使用，如大疆机场 4 接入 DJI O4 地面站上云
     */
    DOCK_SUB_DEVICE(9)

    ;

    private final int domain;

    DeviceDomainEnum(int domain) {
        this.domain = domain;
    }

    @JsonCreator
    public static DeviceDomainEnum find(int domain) {
        return Arrays.stream(values()).filter(domainEnum -> domainEnum.domain == domain).findAny()
                .orElseThrow(() -> new CloudSDKException(DeviceDomainEnum.class, domain));
    }

    @JsonValue
    public int getDomain() {
        return domain;
    }
}
