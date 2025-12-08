package com.dji.sample.cloudapi.client;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.dji.sample.cloudapi.model.enums.DeviceCategory;
import com.dji.sample.cloudapi.model.param.*;
import com.dji.sample.cloudapi.util.ApiUtil;
import com.dji.sample.cloudapi.util.ClientUri;
import com.dji.sample.component.mqtt.model.EventsReceiver;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.model.dto.WorkspaceDTO;
import com.dji.sample.manage.service.IDeviceRedisService;
import com.dji.sample.manage.service.IWorkspaceService;
import com.dji.sample.wayline.service.IWaylineRedisService;
import com.dji.sdk.cloudapi.control.MeteringModeEnum;
import com.dji.sdk.cloudapi.device.*;
import com.dji.sdk.cloudapi.wayline.FlighttaskProgress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * 设备遥感信息客户端
 *
 * @author Qfei
 * @date 2022/12/19 11:42
 */
@Slf4j
@Component
@EnableAsync
@RequiredArgsConstructor
public class DeviceClient extends AbstractClient {

    private final IWaylineRedisService waylineRedisService;
    private final IDeviceRedisService deviceRedisService;
    private final IWorkspaceService workspaceService;

    public void reportOnline(Optional<DeviceDTO> deviceDTO) {
        if (deviceDTO.isEmpty()) {
            return;
        }
        Optional<WorkspaceDTO> workspace = workspaceService.getWorkspaceByWorkspaceId(deviceDTO.get().getWorkspaceId());
        reportDeviceBind(deviceDTO, workspace.map(WorkspaceDTO::getBindCode).orElse(null));
    }

    @Async("asyncThreadPool")
    public void reportDeviceBind(Optional<DeviceDTO> deviceDTOOpt, String bindCode) {
        try {
            deviceDTOOpt.ifPresent(deviceDTO -> {
                // 暂时只维护无人机、遥控器、机场的上线
                String category = DeviceCategory.getCategory(deviceDTO.getDomain().getDomain()).getCode();
                if (StringUtils.hasText(category)) {
                    DeviceOnlineParam.DeviceOnlineParamBuilder onlineParamBuild = DeviceOnlineParam.builder()
                            .sn(deviceDTO.getDeviceSn())
                            .name(StrUtil.blankToDefault(deviceDTO.getNickname(), deviceDTO.getDeviceName()))
                            .category(category)
                            .type(deviceDTO.getDeviceName())
                            .firmwareVersion(deviceDTO.getFirmwareVersion())
                            .time(LocalDateTime.now().format(FORMATTER))
                            .bindCode(bindCode)
                            .childDeviceSn(deviceDTO.getChildDeviceSn());

                    // 组织ID对应运维ops中用户组织ID（主键，不是组织编码）
                    if (StringUtils.hasText(deviceDTO.getOrganizationId())) {
                        try {
                            onlineParamBuild.orgId(Long.valueOf(deviceDTO.getOrganizationId()));
                        } catch (NumberFormatException e) {
                            log.error("设备上线组织ID解析失败，OrgId：{}", deviceDTO.getOrganizationId());
                        }
                    }
                    this.applicationJsonPost(ClientUri.URI_DEVICE_ONLINE, onlineParamBuild.build());
                }
            });
        } catch (Exception e) {
            log.error("设备上线失败, deviceSn: {}", deviceDTOOpt, e);
        }
    }

    @Async("asyncThreadPool")
    public void reportDeviceOffline(String deviceSn) {
        this.applicationJsonPost(ClientUri.URI_DEVICE_OFFLINE,
                DeviceOfflineParam.builder().sn(deviceSn).time(LocalDateTime.now().format(FORMATTER)).build());
    }

    /**
     * Report the OSD information of Dock drone.
     *
     * @param data     OSD data of MQTT received.
     * @param deviceSn Drone SN
     * @param dockSn Dock SN
     */
    @Async("asyncThreadPool")
    public void reportDockDroneOsdInfo(OsdDockDrone data, String deviceSn, String dockSn) {

        AircraftOsdParam.AircraftOsdParamBuilder builder = AircraftOsdParam.builder()
                .sn(deviceSn)
                .firmwareVersion(data.getFirmwareVersion())
                .modelCode(data.getModeCode())
                .longitude(data.getLongitude())
                .latitude(data.getLatitude())
                .horizontalSpeed(data.getHorizontalSpeed())
                .verticalSpeed(data.getVerticalSpeed())
                .elevation(data.getElevation())
                .altitude(data.getHeight())
                .battery(data.getBattery().getCapacityPercent())
                .aircraftDirection(data.getAttitudeHead())
                .aircraftCourse(Optional.ofNullable(data.getAttitudeHead()).map(ApiUtil::course2direction).orElse(null))
                .aircraftPitch(data.getAttitudePitch())
                .aircraftRoll(data.getAttitudeRoll())
                .aircraftYaw(data.getAttitudeHead())
                .homeDistance(data.getHomeDistance())
                .time(System.currentTimeMillis())
                .trackId(data.getTrackId())
                .parentSn(dockSn);

        // obtain main gimbal(the index of 0) osd information.
        Optional.ofNullable(data.getPayloads())
                .flatMap(payloads -> payloads.parallelStream()
                        .filter(payload -> payload.getPayloadIndex().getPosition() == PayloadPositionEnum.FRONT_LEFT)
                        .findAny())
                .ifPresent(mainPayload -> builder
                        .payloadIndex(mainPayload.getPayloadIndex().toString())
                        .gimbalPitch(mainPayload.getGimbalPitch())
                        .gimbalRoll(mainPayload.getGimbalRoll())
                        .gimbalYaw(mainPayload.getGimbalYaw())
                        .measureTargetAltitude(mainPayload.getMeasureTargetAltitude())
                        .measureTargetDistance(mainPayload.getMeasureTargetDistance())
                        .measureTargetLatitude(mainPayload.getMeasureTargetLatitude())
                        .measureTargetLongitude(mainPayload.getMeasureTargetLongitude())
                        .measureTargetErrorState(Optional.ofNullable(mainPayload.getMeasureTargetErrorState())
                                .map(MeasureTargetStateEnum::getState).orElse(null)));

        // 获取ir测距信息
        Optional.ofNullable(data.getCameras())
                .ifPresent(cameras -> {
                    OsdCamera osdCamera = data.getCameras().get(0);
                    builder.irMeteringMode(Objects.requireNonNullElse(osdCamera.getIrMeteringMode(), MeteringModeEnum.DISABLE))
                            .irMeteringPoint(osdCamera.getIrMeteringPoint())
                            .irMeteringArea(osdCamera.getIrMeteringArea());
                });

        // 根据网关SN查询是否是机场飞行作业, 赋值作业ID
        Optional<EventsReceiver<FlighttaskProgress>> runningJobOpt = waylineRedisService.getRunningWaylineJob(dockSn);
        runningJobOpt.ifPresent(x -> builder.sortiesId(x.getOutput().getExt().getFlightId()));

        this.applicationJsonPost(ClientUri.URI_OSD_STATE, builder.build(), DeviceCategory.AIRCRAFT.getCode());
    }

    /**
     * Report the OSD information of drone.
     *
     * @param data     OSD data of MQTT received.
     * @param deviceSn Drone SN
     */
    @Async("asyncThreadPool")
    public void reportRcDroneOsdInfo(OsdRcDrone data, String deviceSn) {

        AircraftOsdParam.AircraftOsdParamBuilder builder = AircraftOsdParam.builder()
                .sn(deviceSn)
                .firmwareVersion(data.getFirmwareVersion())
                .modelCode(data.getModeCode())
                .longitude(data.getLongitude())
                .latitude(data.getLatitude())
                .horizontalSpeed(data.getHorizontalSpeed())
                .verticalSpeed(data.getVerticalSpeed())
                .elevation(data.getElevation())
                .altitude(data.getHeight())
                .battery(data.getBattery().getCapacityPercent())
                .aircraftDirection(data.getAttitudeHead())
                .aircraftCourse(Optional.ofNullable(data.getAttitudeHead()).map(ApiUtil::course2direction).orElse(null))
                .aircraftPitch(data.getAttitudePitch())
                .aircraftRoll(data.getAttitudeRoll())
                .aircraftYaw(data.getAttitudeHead())
                .homeDistance(data.getHomeDistance())
                .time(System.currentTimeMillis())
                .trackId(data.getTrackId());

        // obtain main gimbal(the index of 0) osd information.
        Optional.ofNullable(data.getPayloads())
                .flatMap(payloads -> payloads.parallelStream()
                        .filter(payload -> payload.getPayloadIndex().getPosition() == PayloadPositionEnum.FRONT_LEFT)
                        .findAny())
                .ifPresent(mainPayload -> builder.gimbalPitch(mainPayload.getGimbalPitch())
                        .gimbalRoll(mainPayload.getGimbalRoll())
                        .gimbalYaw(mainPayload.getGimbalYaw()));

        this.applicationJsonPost(ClientUri.URI_OSD_STATE, builder.build(), DeviceCategory.AIRCRAFT.getCode());
    }

    @Async("asyncThreadPool")
    public void reportDockOsdInfo(OsdDock data, String sn) {

        Optional<OsdDock> oldDockOpt = deviceRedisService.getDeviceOsd(sn, OsdDock.class);
        oldDockOpt.ifPresent(osdDock -> {
            BeanUtil.copyProperties(data, osdDock, CopyOptions.create().setIgnoreNullValue(true));
            log.debug("Report the Dock osd info: {}", osdDock);
            DockOsdParam.DockOsdParamBuilder builder = DockOsdParam.builder()
                    .sn(sn)
                    .longitude(osdDock.getLongitude())
                    .latitude(osdDock.getLatitude())
                    .height(osdDock.getHeight())
                    .modelCode(osdDock.getModeCode())
                    .coverState(osdDock.getCoverState())
                    .putterState(osdDock.getPutterState())
                    .supplementLightState(Objects.isNull(osdDock.getSupplementLightState()) ? null
                            : BooleanUtil.toInteger(osdDock.getSupplementLightState()))
                    .droneInDock(Objects.isNull(osdDock.getDroneInDock()) ? null : BooleanUtil.toInteger(osdDock.getDroneInDock()))
                    .activationTime(osdDock.getActivationTime())
                    .batteryStoreMode(osdDock.getBatteryStoreMode())
                    .alarmState(Objects.isNull(osdDock.getAlarmState()) ? null : BooleanUtil.toInteger(osdDock.getAlarmState()))
                    .rainfall(osdDock.getRainfall())
                    .windSpeed(osdDock.getWindSpeed())
                    .environmentTemperature(osdDock.getEnvironmentTemperature())
                    .temperature(osdDock.getTemperature())
                    .humidity(osdDock.getHumidity())
                    .jobNumber(osdDock.getJobNumber())
                    .emergencyStopState(Optional.ofNullable(osdDock.getEmergencyStopState()).map(x -> x ? 1 : 0).orElse(0))
                    .time(System.currentTimeMillis())
                    .drcState(Optional.ofNullable(osdDock.getDrcState()).map(DrcStateEnum::getState).orElse(0))
                    .remainUpload(Optional.ofNullable(osdDock.getMediaFileDetail()).map(MediaFileDetail::getRemainUpload).orElse(null))
                    .electricSupplyVoltage(osdDock.getElectricSupplyVoltage())
                    .workingVoltage(osdDock.getWorkingVoltage())
                    .workingCurrent(osdDock.getWorkingCurrent());

            Optional.ofNullable(osdDock.getAirConditioner()).ifPresent(x ->
                    builder.airConditionerMode(x.getAirConditionerState()).switchTime(x.getSwitchTime()));
            Optional.ofNullable(osdDock.getStorage()).ifPresent(x ->
                    builder.storageTotal(osdDock.getStorage().getTotal()).storageUsed(osdDock.getStorage().getUsed()));
            Optional.ofNullable(osdDock.getNetworkState()).ifPresent(x ->
                    builder.networkType(x.getType()).networkRate(x.getRate()).networkQuality(x.getQuality()));
            Optional.ofNullable(osdDock.getDroneChargeState()).ifPresent(x ->
                    builder.droneBatteryPercent(x.getCapacityPercent()).droneBatteryState(BooleanUtil.toInteger(x.getState())));
            Optional.ofNullable(osdDock.getDroneBatteryMaintenanceInfo()).ifPresent(x ->
                    builder.droneBatteryMaintenanceState(x.getMaintenanceState()).droneBatteryMaintenanceTimeLeft(x.getMaintenanceTimeLeft()));
            Optional.ofNullable(osdDock.getBackupBattery()).ifPresent(x ->
                    builder.backupBatterySwitch(BooleanUtil.toInteger(x.getBatterySwitch())).backupBatteryVoltage(x.getVoltage()));

            this.applicationJsonPost(ClientUri.URI_OSD_STATE, builder.build(), DeviceCategory.DOCK.getCode());
        });
    }

    @Async("asyncThreadPool")
    public void reportRcOsdInfo(OsdRemoteControl data, DeviceDTO deviceDTO) {
        this.applicationJsonPost(ClientUri.URI_OSD_STATE, RcOsdParam.builder()
                .sn(deviceDTO.getDeviceSn())
                .firmwareVersion(deviceDTO.getFirmwareVersion())
                .longitude(data.getLongitude())
                .latitude(data.getLatitude())
                .batteryPercent(data.getCapacityPercent())
                .time(System.currentTimeMillis())
                .build(), DeviceCategory.RC.getCode());
    }

}
