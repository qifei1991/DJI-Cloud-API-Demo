package com.dji.sample.cloudapi.model.param;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 无人机状态信息参数
 *
 * @author Qfei
 * @date 2022/11/23 13:34
 */
@Data
@Builder
public class AircraftOsdParam {
    /**
     * 飞行记录ID（架次ID）
     */
    private String sortiesId;
    @NotBlank(message = "设备SN不能为空")
    private String sn;
    private String firmwareVersion;
    private Integer modelCode;
    private Double longitude;
    private Double latitude;
    private Float horizontalSpeed;
    private Float verticalSpeed;
    private Double elevation;
    private Double altitude;
    private Integer battery;
    private Float aircraftDirection;
    private Double aircraftCourse;
    private Double aircraftPitch;
    private Double aircraftRoll;
    private Double aircraftYaw;
    private Double gimbalPitch;
    private Double gimbalRoll;
    private Double gimbalYaw;
    private Double homeLongitude;
    private Double homeLatitude;
    private Double homeHeight;
    private Double homeDistance;
    private Long time;
}