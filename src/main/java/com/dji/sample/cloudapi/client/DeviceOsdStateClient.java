package com.dji.sample.cloudapi.client;

import com.dji.sample.cloudapi.model.enums.DeviceCategory;
import com.dji.sample.cloudapi.model.param.AircraftOsdParam;
import com.dji.sample.cloudapi.model.param.DeviceOfflineParam;
import com.dji.sample.cloudapi.model.param.DeviceOnlineParam;
import com.dji.sample.cloudapi.model.param.DockOsdParam;
import com.dji.sample.cloudapi.model.param.RcOsdParam;
import com.dji.sample.cloudapi.util.ClientUri;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.model.enums.DeviceDomainEnum;
import com.dji.sample.manage.model.receiver.OsdDockReceiver;
import com.dji.sample.manage.model.receiver.OsdGatewayReceiver;
import com.dji.sample.manage.model.receiver.OsdSubDeviceReceiver;
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
public class DeviceOsdStateClient extends AbstractClient {

    @Async("asyncThreadPool")
    public void reportOnline(Optional<DeviceDTO> deviceDTOOptional) {
        deviceDTOOptional.ifPresent(deviceDTO -> {
            // 暂时只维护无人机、遥控器、机场的上线
            String domainDesc = deviceDTO.getDomain();
            String category = null;
            switch (DeviceDomainEnum.getDomain(domainDesc)) {
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
                        .name(deviceDTO.getDeviceName())
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
     * @param sn drone SN
     */
    @Async("asyncThreadPool")
    public void reportDroneOsdInfo(OsdSubDeviceReceiver data, String sn) {
        AircraftOsdParam.AircraftOsdParamBuilder builder = AircraftOsdParam.builder()
                .sn(sn)
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
                .aircraftPitch(data.getAttitudePitch())
                .aircraftRoll(data.getAttitudeRoll())
                .homeDistance(data.getHomeDistance())
                .time(System.currentTimeMillis());
        // obtain main gimbal osd information.
        Optional.ofNullable(data.getPayloads())
                .ifPresent(payloads -> payloads.parallelStream()
                        .filter(osdPayloadReceiver -> "0".equals(osdPayloadReceiver.getPayloadIndex()))
                        .findFirst()
                        .ifPresent(mainPayload -> builder.gimbalPitch(mainPayload.getGimbalPitch())
                                .gimbalRoll(mainPayload.getGimbalRoll())
                                .gimbalYaw(mainPayload.getGimbalYaw())));
        this.applicationJsonPost(ClientUri.URI_OSD_STATE, builder.build(), DeviceCategory.AIRCRAFT.getCode());
    }

    @Async("asyncThreadPool")
    public void reportDockOsdInfo(OsdDockReceiver data, String sn) {
        this.applicationJsonPost(ClientUri.URI_OSD_STATE, DockOsdParam.builder()
                .sn(sn)
                .longitude(data.getLongitude())
                .latitude(data.getLatitude())
                .modelCode(data.getModeCode())
                .coverState(data.getCoverState())
                .putterState(data.getPutterState())
                .supplementLightState(data.getSupplementLightState())
                .networkRate(data.getNetworkState().getRate())
                .droneInDock(data.getDroneInDock())
                .activationTime(data.getActivationTime())
                .batteryStoreMode(data.getBatteryStoreMode())
                .alarmState(data.getAlarmState())
                .droneBatteryPercent(data.getDroneChargeState().getCapacityPercent())
                .droneBatteryState(data.getDroneChargeState().getState())
                .droneBatteryMaintenanceState(data.getDroneBatteryMaintenanceInfo().getMaintenanceState())
                .droneBatteryMaintenanceTimeLeft(data.getDroneBatteryMaintenanceInfo().getMaintenanceTimeLeft())
                .backupBatterySwitch(data.getBackupBattery().getBatterySwitch())
                .backupBatteryVoltage(data.getBackupBattery().getVoltage())
                .emergencyStopState(data.getEmergencyStopState())
                .time(System.currentTimeMillis())
                .build(), DeviceCategory.DOCK.getCode());
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
