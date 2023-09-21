package com.dji.sample.cloudapi.client;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.dji.sample.cloudapi.model.enums.DeviceCategory;
import com.dji.sample.cloudapi.model.param.*;
import com.dji.sample.cloudapi.util.ApiUtil;
import com.dji.sample.cloudapi.util.ClientUri;
import com.dji.sample.component.redis.RedisConst;
import com.dji.sample.component.redis.RedisOpsUtils;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.model.enums.DeviceDomainEnum;
import com.dji.sample.manage.model.enums.DockDrcStateEnum;
import com.dji.sample.manage.model.receiver.DockMediaFileDetailReceiver;
import com.dji.sample.manage.model.receiver.OsdDockReceiver;
import com.dji.sample.manage.model.receiver.OsdGatewayReceiver;
import com.dji.sample.manage.model.receiver.OsdSubDeviceReceiver;
import com.dji.sample.wayline.model.dto.WaylineJobDTO;
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
public class DeviceOsdStateClient extends AbstractClient {

    @Async("asyncThreadPool")
    public void reportOnline(Optional<DeviceDTO> deviceDTOOptional) {
        deviceDTOOptional.ifPresent(deviceDTO -> {
            // 暂时只维护无人机、遥控器、机场的上线
            String category = null;
            switch (DeviceDomainEnum.find(deviceDTO.getDomain())) {
                case SUB_DEVICE:
                    category = DeviceCategory.AIRCRAFT.getCode();
                    break;
                case DOCK:
                    category = DeviceCategory.DOCK.getCode();
                    break;
                case GATEWAY:
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
     * @param data OSD data of MQTT received.
     * @param deviceSn drone SN
     * @param jobOpt 飞行任务对象
     */
    @Async("asyncThreadPool")
    public void reportDroneOsdInfo(OsdSubDeviceReceiver data, String deviceSn, Optional<WaylineJobDTO> jobOpt) {
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
                        .filter(osdPayloadReceiver -> Optional.ofNullable(osdPayloadReceiver.getPayloadIndex()).isPresent()
                                && osdPayloadReceiver.getPayloadIndex().endsWith("-0"))
                        .findAny())
                .ifPresent(mainPayload -> builder.gimbalPitch(mainPayload.getGimbalPitch())
                        .gimbalRoll(mainPayload.getGimbalRoll())
                        .gimbalYaw(mainPayload.getGimbalYaw()));

        jobOpt.ifPresent(x -> builder.sortiesId(x.getJobId()));

        this.applicationJsonPost(ClientUri.URI_OSD_STATE, builder.build(), DeviceCategory.AIRCRAFT.getCode());
    }

    @Async("asyncThreadPool")
    public void reportDockOsdInfo(OsdDockReceiver receiver, String sn) {

        OsdDockReceiver data = (OsdDockReceiver) RedisOpsUtils.get(RedisConst.OSD_PREFIX + sn);
        BeanUtil.copyProperties(receiver, data, CopyOptions.create().setIgnoreNullValue(true));

        DockOsdParam.DockOsdParamBuilder builder = DockOsdParam.builder()
                .sn(sn)
                .longitude(data.getLongitude())
                .latitude(data.getLatitude())
                .height(data.getHeight())
                .modelCode(data.getModeCode())
                .coverState(data.getCoverState())
                .putterState(data.getPutterState())
                .supplementLightState(data.getSupplementLightState())
                .droneInDock(data.getDroneInDock())
                .activationTime(data.getActivationTime())
                .batteryStoreMode(data.getBatteryStoreMode())
                .alarmState(data.getAlarmState())
                .rainfall(data.getRainfall())
                .windSpeed(data.getWindSpeed())
                .environmentTemperature(data.getEnvironmentTemperature())
                .temperature(data.getTemperature())
                .humidity(data.getHumidity())
                .jobNumber(data.getJobNumber())
                .emergencyStopState(Optional.ofNullable(data.getEmergencyStopState()).map(x -> x ? 1 : 0).orElse(0))
                .time(System.currentTimeMillis())
                .drcState(Optional.ofNullable(data.getDrcState()).map(DockDrcStateEnum::getVal).orElse(0))
                .remainUpload(Optional.ofNullable(data.getMediaFileDetail()).map(DockMediaFileDetailReceiver::getRemainUpload).orElse(null))
                .electricSupplyVoltage(data.getElectricSupplyVoltage())
                .workingVoltage(data.getWorkingVoltage())
                .workingCurrent(data.getWorkingCurrent())
                .airConditionerMode(data.getAirConditionerMode());
        Optional.ofNullable(data.getStorage()).ifPresent(x ->
                builder.storageTotal(data.getStorage().getTotal()).storageUsed(data.getStorage().getUsed()));
        Optional.ofNullable(data.getNetworkState()).ifPresent(x ->
                builder.networkType(x.getType()).networkRate(x.getRate()).networkQuality(x.getQuality()));
        Optional.ofNullable(data.getDroneChargeState()).ifPresent(x ->
                builder.droneBatteryPercent(x.getCapacityPercent()).droneBatteryState(x.getState()));
        Optional.ofNullable(data.getDroneBatteryMaintenanceInfo()).ifPresent(x ->
                builder.droneBatteryMaintenanceState(x.getMaintenanceState()).droneBatteryMaintenanceTimeLeft(x.getMaintenanceTimeLeft()));
        Optional.ofNullable(data.getBackupBattery()).ifPresent(x ->
                builder.backupBatterySwitch(x.getBatterySwitch()).backupBatteryVoltage(x.getVoltage()));

        this.applicationJsonPost(ClientUri.URI_OSD_STATE, builder.build(), DeviceCategory.DOCK.getCode());
    }

    @Async("asyncThreadPool")
    public void reportRcOsdInfo(OsdGatewayReceiver data, DeviceDTO deviceDTO) {
        this.applicationJsonPost(ClientUri.URI_OSD_STATE, RcOsdParam.builder()
                .sn(deviceDTO.getDeviceSn())
                .firmwareVersion(deviceDTO.getFirmwareVersion())
                .longitude(data.getLongitude())
                .latitude(data.getLatitude())
                .batteryPercent(data.getRemainPower())
                .time(System.currentTimeMillis())
                .build(), DeviceCategory.RC.getCode());
    }

}
