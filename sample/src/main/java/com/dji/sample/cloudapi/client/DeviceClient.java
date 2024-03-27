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
import com.dji.sample.component.redis.RedisConst;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.service.IDeviceRedisService;
import com.dji.sample.wayline.service.IWaylineRedisService;
import com.dji.sdk.cloudapi.device.*;
import com.dji.sdk.cloudapi.wayline.FlighttaskProgress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
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

    @Async("asyncThreadPool")
    public void reportOnline(Optional<DeviceDTO> deviceDTOOptional) {
        deviceDTOOptional.ifPresent(deviceDTO -> {
            // 暂时只维护无人机、遥控器、机场的上线
            String category = null;
            switch (DeviceDomainEnum.find(deviceDTO.getDomain().getDomain())) {
                case DRONE:
                    category = DeviceCategory.AIRCRAFT.getCode();
                    break;
                case DOCK:
                    category = DeviceCategory.DOCK.getCode();
                    break;
                case REMOTER_CONTROL:
                    category = DeviceCategory.RC.getCode();
                    break;
                default:
                    break;
            }
            if (StringUtils.hasText(category)) {
                this.applicationJsonPost(ClientUri.URI_DEVICE_ONLINE, DeviceOnlineParam.builder()
                        .sn(deviceDTO.getDeviceSn())
                        .name(StrUtil.blankToDefault(deviceDTO.getNickname(), deviceDTO.getDeviceName()))
                        .category(category)
                        .type(deviceDTO.getDeviceName())
                        .firmwareVersion(deviceDTO.getFirmwareVersion())
                        .time(LocalDateTime.now().format(FORMATTER))
                        .build());
            }
        });
    }

    @Async("asyncThreadPool")
    public void reportDeviceOffline(String deviceSn) {
        this.applicationJsonPost(ClientUri.URI_DEVICE_OFFLINE,
                DeviceOfflineParam.builder().sn(deviceSn).time(LocalDateTime.now().format(FORMATTER)).build());
    }

    /**
     * Report the OSD information of drone.
     *
     * @param data     OSD data of MQTT received.
     * @param deviceSn Drone SN
     * @param dockSn Dock SN
     */
    @Async("asyncThreadPool")
    public void reportDroneOsdInfo(OsdDockDrone data, String deviceSn, String dockSn) {

        // 根据网关SN查询是否是机场飞行任务, 赋值作业ID
        Optional<EventsReceiver<FlighttaskProgress>> runningJobOpt = waylineRedisService.getRunningWaylineJob(dockSn);
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

        runningJobOpt.ifPresent(x -> builder.sortiesId(x.getBid()));

        this.applicationJsonPost(ClientUri.URI_OSD_STATE, builder.build(), DeviceCategory.AIRCRAFT.getCode());
    }

    @Async("asyncThreadPool")
    public void reportDockOsdInfo(OsdDock data, String sn) {

        Optional<OsdDock> oldDockOpt = deviceRedisService.getDeviceOsd(RedisConst.OSD_PREFIX + sn, OsdDock.class);
        oldDockOpt.ifPresent(x -> BeanUtil.copyProperties(data, x, CopyOptions.create().setIgnoreNullValue(true)));
        OsdDock osdDock = oldDockOpt.get();

        DockOsdParam.DockOsdParamBuilder builder = DockOsdParam.builder()
                .sn(sn)
                .longitude(osdDock.getLongitude())
                .latitude(osdDock.getLatitude())
                .height(osdDock.getHeight())
                .modelCode(osdDock.getModeCode())
                .coverState(osdDock.getCoverState())
                .putterState(osdDock.getPutterState())
                .supplementLightState(BooleanUtil.toInteger(osdDock.getSupplementLightState()))
                .droneInDock(BooleanUtil.toInteger(osdDock.getDroneInDock()))
                .activationTime(osdDock.getActivationTime())
                .batteryStoreMode(osdDock.getBatteryStoreMode())
                .alarmState(BooleanUtil.toInteger(osdDock.getAlarmState()))
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
