package com.dji.sample.wayline.model.dto;

import com.dji.sdk.cloudapi.device.DroneModeCodeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 飞机返航监控参数
 *
 * @author Qfei
 * @date 2026/4/20 13:54
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class DroneReturnHomeMonitor {

    /**
     * 定时监控任务ID
     */
    private String taskId;

    /**
     * 航线飞行任务ID
     */
    private String jobId;

    /**
     * 机场SN
     */
    private String dockSn;

    /**
     * 飞机序列号
     */
    private String droneSn;

    private DroneModeCodeEnum droneModeCodeEnum;

    /**
     * 落在舱外时，飞机经纬度和电池电量
     */
    private Float latitude;

    private Float longitude;

    private Float altitude;

    private Integer batteryCapacityPercent;

    @Override
    public String toString() {
        return "DroneReturnHomeMonitor{" +
                "taskId='" + taskId + '\'' +
                ", jobId='" + jobId + '\'' +
                ", dockSn='" + dockSn + '\'' +
                ", droneSn='" + droneSn + '\'' +
                ", droneModeCodeEnum=" + droneModeCodeEnum +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", batteryCapacityPercent=" + batteryCapacityPercent +
                '}';
    }
}
