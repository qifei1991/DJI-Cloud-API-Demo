package com.dji.sample.psdk.service.impl;

import cn.hutool.core.io.file.FileNameUtil;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.model.param.DeviceQueryParam;
import com.dji.sample.manage.service.IDeviceRedisService;
import com.dji.sample.manage.service.IDeviceService;
import com.dji.sample.psdk.model.dto.PsdkWidgetValuesDTO;
import com.dji.sample.psdk.model.param.BasePlaySetParam;
import com.dji.sample.psdk.service.IPsdkService;
import com.dji.sample.psdk.service.IPsdkWidgetRedisService;
import com.dji.sdk.cloudapi.device.DeviceDomainEnum;
import com.dji.sdk.cloudapi.device.PsdkNameEnum;
import com.dji.sdk.cloudapi.device.PsdkWidget;
import com.dji.sdk.common.SDKManager;
import com.dji.sdk.config.version.GatewayManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Qfei
 * @date 2025/8/8 17:57
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PsdkServiceImpl implements IPsdkService {

    private final IDeviceService deviceService;
    private final IDeviceRedisService deviceRedisService;
    private final IPsdkWidgetRedisService psdkWidgetRedisService;
    private final MzPilotSpeakerService mzPilotSpeakerService;

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

    @Override
    public Integer getSetParamPsdkIndex(BasePlaySetParam setParam) {
        log.info("获取喊话器PayloadIndex的值, SN: {}", setParam.getDeviceSn());
        if (mzPilotSpeakerService.isDroneSn(setParam.getDeviceSn())) {
            mzPilotSpeakerService.subscribe(setParam.getDeviceSn());
            log.info("飞控喊话, PayloadIndex: {}", setParam.getPsdkIndex());
            if (Objects.isNull(setParam.getPsdkIndex())) {
                throw new RuntimeException("喊话器负载索引位置参数为空，请查证！");
            }
            return setParam.getPsdkIndex();
        } else {
            GatewayManager gatewayManager = SDKManager.getDeviceSDK(setParam.getDeviceSn());
            if (!StringUtils.hasText(gatewayManager.getDroneSn())) {
                throw new RuntimeException("机场设备不在线");
            }
            Optional<List<PsdkWidget>> dronePsdkValues = psdkWidgetRedisService.getPsdkWidgetValues(gatewayManager.getDroneSn());
            if (dronePsdkValues.isEmpty()) {
                throw new RuntimeException("设备不存在psdk负载");
            }
            log.info("PsdkWidget: {}", dronePsdkValues.get());
            Optional<PsdkWidget> speakerOpt = dronePsdkValues.get()
                    .stream()
                    .filter(x -> PsdkNameEnum.SPEAKER == x.getPsdkName())
                    .findFirst();
            if (speakerOpt.isEmpty()) {
                throw new RuntimeException("设备不存在喊话器");
            }
            log.info("机场喊话, PayloadIndex：{}", speakerOpt.get().getPsdkIndex());
            return speakerOpt.get().getPsdkIndex();
        }
    }

    public boolean isAudioFile(String filename) {
        return FileNameUtil.isType(filename, "mp3", "pcm", "wav", "amr");
    }
}
