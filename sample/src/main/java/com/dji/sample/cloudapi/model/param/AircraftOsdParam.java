package com.dji.sample.cloudapi.model.param;

import com.dji.sdk.cloudapi.control.MeteringModeEnum;
import com.dji.sdk.cloudapi.device.DroneModeCodeEnum;
import com.dji.sdk.cloudapi.device.IrMeteringArea;
import com.dji.sdk.cloudapi.device.IrMeteringPoint;
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
    private DroneModeCodeEnum modelCode;
    private Float longitude;
    private Float latitude;
    private Float horizontalSpeed;
    private Float verticalSpeed;
    private Float elevation;
    private Float altitude;
    private Integer battery;
    private Float aircraftDirection;
    private String aircraftCourse;
    private Double aircraftPitch;
    private Double aircraftRoll;
    private Float aircraftYaw;
    private Double homeLongitude;
    private Double homeLatitude;
    private Double homeHeight;
    private Float homeDistance;
    private Long time;

    private Integer modeCodeReason;

    private String trackId;

    private MeteringModeEnum irMeteringMode;

    private IrMeteringPoint irMeteringPoint;

    private IrMeteringArea irMeteringArea;

    private String parentSn;    // 网关设备sn（机场或者遥控器）

    // 云台负载信息 start
    private String payloadIndex;

    private Float gimbalPitch;
    private Float gimbalRoll;
    private Float gimbalYaw;

    private Float measureTargetAltitude;
    private Float measureTargetDistance;
    private Float measureTargetLatitude;
    private Float measureTargetLongitude;
    private Integer measureTargetErrorState;
    // 云台负载信息 end

    // 相机信息
    private Float zoomFactor;
    private Float irZoomFactor;
}
