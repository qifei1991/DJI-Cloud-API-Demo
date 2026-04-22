package com.dji.sdk.cloudapi.device;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * @author sean
 * @version 1.4
 * @date 2023/3/9
 */
public enum DroneModeCodeEnum {
    /**
     * {
     * "0":"待机",
     * "1":"起飞准备",
     * "2":"起飞准备完毕",
     * "3":"手动飞行",
     * "4":"自动起飞",
     * "5":"航线飞行",
     * "6":"全景拍照",
     * "7":"智能跟随",
     * "8":"ADS-B 躲避",
     * "9":"自动返航",
     * "10":"自动降落",
     * "11":"强制降落",
     * "12":"三桨叶降落",
     * "13":"升级中",
     * "14":"未连接",
     * "15":"APAS",
     * "16":"虚拟摇杆状态",
     * "17":"指令飞行",
     * "18":"空中 RTK 收敛模式",
     * "19":"机场选址中",
     * "20":"POI环绕",
     * "21":"进离场航线飞行过程中"}
     */

    IDLE(0),

    TAKEOFF_PREPARE(1),

    TAKEOFF_FINISHED(2),

    MANUAL(3),

    TAKEOFF_AUTO(4),

    WAYLINE(5),

    PANORAMIC_SHOT(6),

    ACTIVE_TRACK(7),

    ADS_B_AVOIDANCE(8),

    RETURN_AUTO(9),

    LANDING_AUTO(10),

    LANDING_FORCED(11),

    LANDING_THREE_PROPELLER(12),

    UPGRADING(13),

    DISCONNECTED(14),

    APAS(15),

    VIRTUAL_JOYSTICK(16),

    LIVE_FLIGHT_CONTROLS(17),

    AERIAL_RTK_FIXED(18),

    DOCK_SITE_EVALUATION(19),

    POI(20),

    ;

    private final int code;

    DroneModeCodeEnum(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    @JsonCreator
    public static DroneModeCodeEnum find(int code) {
        return Arrays.stream(values()).filter(modeCodeEnum -> modeCodeEnum.ordinal() == code).findAny()
                .orElseThrow(() -> new CloudSDKException(DroneModeCodeEnum.class, code));
    }
}
