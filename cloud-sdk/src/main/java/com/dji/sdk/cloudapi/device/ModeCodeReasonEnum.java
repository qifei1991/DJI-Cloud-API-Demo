package com.dji.sdk.cloudapi.device;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * @author sean
 * @version 1.4
 * @date 2023/2/28
 */
public enum ModeCodeReasonEnum {

    /**
     * {
     * "0":"无意义",
     * "1":"电池电量不足（返航、降落）",
     * "2":"电池电压不足（返航、降落）",
     * "3":"电压严重过低（返航、降落）",
     * "4":"遥控器按键请求（起飞、返航、降落）",
     * "5":"App 请求（起飞、返航、降落）",
     * "6":"遥控信号丢失（返航、降落、悬停）",
     * "7":"导航、SDK 等外部设备触发（起飞、返航、降落）",
     * "8":"进入机场限飞区（降落）",
     * "9":"虽然触发了返航但是因为距离 Home 点距离太近（降落）",
     * "10":"虽然触发了返航但是因为距离 Home 点距离太远（降落）",
     * "11":"执行航点任务时请求（起飞）",
     * "12":"返航阶段到达 Home 点上方后请求（降落）",
     * "13":"飞行器高度下降，距地面 0.7m（二段降落限低）时，继续下降导致（降落）",
     * "14":"App、SDK 等设备强制突破限低保护进行（降落）",
     * "15":"因为周围有航班经过而请求（返航、降落）",
     * "16":"因为高度控制失败请求（返航、降落）",
     * "17":"智能低电量返航后进入（降落）",
     * "18":"AP控制飞行模式（手动飞行）",
     * "19":"硬件异常（返航、降落）",
     * "20":"防触地保护结束（降落）",
     * "21":"返航取消 (悬停)",
     * "22":"返航时遇到障碍物（降落）",
     * "23":"机场场景下大风触发（返航）"}
     */

    NO_MEANING(0),

    LOW_POWER(1),

    LOW_VOLTAGE(2),

    SERIOUS_LOW_VOLTAGE(3),

    RC_CONTROL(4),

    APP_CONTROL(5),

    RC_SIGNAL_LOST(6),

    EXTERNAL_DEVICE_TRIGGERED(7),

    GEO_ZONE(8),

    HOME_POINT_TOO_CLOSED(9),

    HOME_POINT_TOO_FAR(10),

    EXECUTING_WAYPOINT_MISSION(11),

    ARRIVE_HOME_POINT(12),

    SECOND_LIMIT_LANDING(13),

    APP_FORCIBLY_BREAK_PROTECTION(14),

    PLANES_PASSING_NEARBY(15),

    HEIGHT_CONTROL_FAILED(16),

    LOW_POWER_RTH(17),

    AP_CONTROL(18),

    HARDWARE_ABNORMAL(19),

    TOUCHDOWN_AVOIDANCE_PROTECTION(20),

    CANCEL_RTH(21),

    RTH_OBSTACLE_AVOIDANCE(22),

    RTH_STRONG_GALE(23),

    ;

    private final int reason;

    ModeCodeReasonEnum(int reason) {
        this.reason = reason;
    }

    @JsonValue
    public int getReason() {
        return reason;
    }

    @JsonCreator
    public static ModeCodeReasonEnum find(int reason) {
        return Arrays.stream(values()).filter(reasonEnum -> reasonEnum.reason == reason).findAny()
                .orElseThrow(() -> new CloudSDKException(ModeCodeReasonEnum.class, reason));
    }
}
