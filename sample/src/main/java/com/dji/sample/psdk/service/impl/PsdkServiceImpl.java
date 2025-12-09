package com.dji.sample.psdk.service.impl;

import com.dji.sample.psdk.model.dto.PsdkWidgetValuesDTO;
import com.dji.sample.psdk.service.IPsdkService;
import com.dji.sample.psdk.service.IPsdkWidgetRedisService;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.model.param.DeviceQueryParam;
import com.dji.sample.manage.service.IDeviceRedisService;
import com.dji.sample.manage.service.IDeviceService;
import com.dji.sdk.cloudapi.device.DeviceDomainEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Qfei
 * @date 2025/8/8 17:57
 */
@Service
public class PsdkServiceImpl implements IPsdkService {

    @Autowired
    private IPsdkWidgetRedisService psdkWidgetRedisService;
    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private IDeviceRedisService deviceRedisService;

    @Override
    public List<PsdkWidgetValuesDTO> getPsdkWidgetValues(String workspaceId) {

        // Query all devices in this workspace.
        List<DeviceDTO> devicesList = deviceService.getSimpleDevicesByParams(
                DeviceQueryParam.builder()
                        .workspaceId(workspaceId)
                        .domains(List.of(DeviceDomainEnum.DRONE.getDomain()))
                        .build());
        // Query the live capability of each device.
        return devicesList.stream()
                .filter(device -> deviceRedisService.checkDeviceOnline(device.getDeviceSn()))
                .map(device -> new PsdkWidgetValuesDTO()
                        .setName(Objects.requireNonNullElse(device.getNickname(), device.getDeviceName()))
                        .setSn(device.getDeviceSn())
                        .setPsdkWidgetValues(psdkWidgetRedisService.getPsdkWidgetValues(device.getDeviceSn())
                                .orElse(Collections.emptyList())))
                .collect(Collectors.toList());
    }
}
