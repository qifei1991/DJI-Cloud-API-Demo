package com.dji.sample.cloudapi.model.param;

import lombok.Builder;
import lombok.Data;

/**
 * 机场状态信息对象
 *
 * @author Qfei
 * @date 2022/11/23 13:59
 */
@Data
@Builder
public class DockOsdParam {
    /**
     * 飞行记录ID（架次ID）
     */
    private String sortiesId;
    private String sn;
    private String firmwareVersion;
    private Double longitude;
    private Double latitude;
    /**
     * 机场状态，{"0":"空闲中","1":"现场调试","2":"远程调试","3":"固件升级中","4":"作业中"}
     */
    private Integer modelCode;
    /**
     * 舱盖状态，{"0":"关闭","1":"打开","2":"半开","3":"舱盖状态异常"}
     */
    private Integer coverState;
    /**
     * 推杆状态，{"0":"关闭","1":"打开","2":"半开","3":"推杆状态异常"}
     */
    private Integer putterState;
    /**
     * 补光灯状态，{"0":"关闭","1":"打开"}
     */
    private Integer supplementLightState;
    /**
     * 网络速率kb/s
     */
    private Float networkRate;
    /**
     * 飞机是否在舱
     */
    private Integer droneInDock;
    /**
     * 机场激活时间戳
     */
    private Long activationTime;
    /**
     * 电池存储（保养）模式
     */
    private Integer batteryStoreMode;
    /**
     * 机场声光报警状态，{"0":"声光报警关闭","1":"声光报警开启"}
     */
    private Integer alarmState;
    /**
     * 飞机电量百分比
     */
    private Integer droneBatteryPercent;
    /**
     * 飞机充电状态，{"0":"空闲","1":"充电中"}
     */
    private Integer droneBatteryState;
    /**
     * 飞行器电池保养状态，{"0":"无需保养","1":"待保养","2":"正在保养"}
     */
    private Integer droneBatteryMaintenanceState;
    /**
     * 飞行器电池保养剩余时间，单位：h
     */
    private Long droneBatteryMaintenanceTimeLeft;
    /**
     * 备用电池开关，{"0":"备用电池关闭","1":"备用电池开启"}
     */
    private Integer backupBatterySwitch;
    /**
     * 备用电池电压，{"unit":"毫伏","min":"0","max":"30000","step":"1","desc":"备用电池关闭时电压为0"}
     */
    private Integer backupBatteryVoltage;
    /**
     * 紧急停止按钮状态，{"0":"关闭","1":"开启"}
     */
    private Integer emergencyStopState;

    private Long time;

}