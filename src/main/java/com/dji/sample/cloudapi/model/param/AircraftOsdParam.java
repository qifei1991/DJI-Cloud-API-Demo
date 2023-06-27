package com.dji.sample.cloudapi.model.param;

import com.dji.sample.manage.model.enums.DeviceModeCodeEnum;
import lombok.Builder;
import lombok.Data;

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
    private String sn;
    private String firmwareVersion;
    private DeviceModeCodeEnum modelCode;
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
    private Float aircraftYaw;
    private Double gimbalPitch;
    private Double gimbalRoll;
    private Double gimbalYaw;
    private Double homeLongitude;
    private Double homeLatitude;
    private Double homeHeight;
    private Double homeDistance;
    private Long time;

    private String trackId;
}
