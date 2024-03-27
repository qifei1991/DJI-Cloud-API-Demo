package com.dji.sample.cloudapi.service;

import com.dji.sample.component.redis.RedisConst;
import com.dji.sample.component.redis.RedisOpsUtils;
import com.dji.sample.manage.model.dto.CapacityDeviceDTO;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.model.param.DeviceQueryParam;
import com.dji.sample.manage.service.ICapacityCameraService;
import com.dji.sample.manage.service.IDeviceService;
import com.dji.sdk.cloudapi.device.DeviceDomainEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Qfei
 * @date 2022/12/29 14:28
 */
@Slf4j
@Service
public class LiveService {

    @Autowired
    private ICapacityCameraService capacityCameraService;
    @Autowired
    private IDeviceService deviceService;

    public CapacityDeviceDTO getDeviceLiveCapacity(String sn) {

        if (!RedisOpsUtils.checkExist(RedisConst.DEVICE_ONLINE_PREFIX + sn)) {
            return null;
        }
        // Query all devices in this workspace.
        Optional<DeviceDTO> device = deviceService.getDevicesByParams(DeviceQueryParam.builder()
                .deviceSn(sn)
                .domains(List.of(DeviceDomainEnum.DRONE.getDomain(), DeviceDomainEnum.DOCK.getDomain()))
                .build())
                .stream().findFirst();
        // Query the live capability of online drone.
        if (device.isEmpty()) {
            return null;
        }
        DeviceDTO deviceDTO = device.get();
        return CapacityDeviceDTO.builder()
                .name(Objects.requireNonNullElse(deviceDTO.getNickname(), deviceDTO.getDeviceName()))
                .sn(deviceDTO.getDeviceSn())
                .camerasList(capacityCameraService.getCapacityCameraByDeviceSn(deviceDTO.getDeviceSn()))
                .build();
    }
}
