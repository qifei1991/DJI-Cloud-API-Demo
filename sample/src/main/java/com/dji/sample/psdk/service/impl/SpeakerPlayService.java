package com.dji.sample.psdk.service.impl;

import com.dji.sample.psdk.model.param.SpeakerPlaySetParam;
import com.dji.sample.psdk.service.IPsdkWidgetRedisService;
import com.dji.sample.psdk.service.ISpeakerPlayService;
import com.dji.sdk.cloudapi.device.PsdkNameEnum;
import com.dji.sdk.cloudapi.device.PsdkWidget;
import com.dji.sdk.cloudapi.psdk.SpeakerPlayModeSetRequest;
import com.dji.sdk.cloudapi.psdk.SpeakerPlayVolumeSetRequest;
import com.dji.sdk.common.HttpResultResponse;
import com.dji.sdk.common.SDKManager;
import com.dji.sdk.config.version.GatewayManager;
import com.dji.sdk.mqtt.services.ServicesReplyData;
import com.dji.sdk.mqtt.services.TopicServicesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Qfei
 * @date 2024/8/12 18:06
 */
@Service
@RequiredArgsConstructor
public class SpeakerPlayService implements ISpeakerPlayService {

    private final SDKPsdkPublishService sdkPsdkPublishService;
    private final IPsdkWidgetRedisService psdkWidgetRedisService;
    private final MzPilotSpeakerService mzPilotSpeakerService;

    @Override
    public HttpResultResponse setPlayMode(String workspaceId, SpeakerPlaySetParam setParam) {
        if (Objects.isNull(setParam.getMode())) {
            HttpResultResponse.error("播放模式参数错误");
        }
        Integer psdkIndex = getSetParamPsdkIndex(setParam);
        TopicServicesResponse<ServicesReplyData> serviceReply = sdkPsdkPublishService.speakerPlayModeSet(
                setParam.getDeviceSn(),
                new SpeakerPlayModeSetRequest()
                        .setPsdkIndex(psdkIndex)
                        .setPlayMode(setParam.getMode()));
        if (!serviceReply.getData().getResult().isSuccess()) {
            return HttpResultResponse.error(serviceReply.getData().getResult().getMessage());
        }
        return HttpResultResponse.success();
    }

    @Override
    public HttpResultResponse setPlayVolume(String workspaceId, SpeakerPlaySetParam setParam) {
        if (Objects.isNull(setParam.getVolume())) {
            HttpResultResponse.error("音量参数错误");
        }
        Integer psdkIndex = getSetParamPsdkIndex(setParam);
        TopicServicesResponse<ServicesReplyData> serviceReply = sdkPsdkPublishService.speakerPlayVolumeSet(
                setParam.getDeviceSn(),
                new SpeakerPlayVolumeSetRequest()
                        .setPsdkIndex(psdkIndex)
                        .setPlayVolume(setParam.getVolume()));
        if (!serviceReply.getData().getResult().isSuccess()) {
            return HttpResultResponse.error(serviceReply.getData().getResult().getMessage());
        }
        return HttpResultResponse.success();
    }

    private Integer getSetParamPsdkIndex(SpeakerPlaySetParam setParam) {
        if (mzPilotSpeakerService.isDroneSn(setParam.getDeviceSn())) {
            mzPilotSpeakerService.subscribe(setParam.getDeviceSn());
            return setParam.getPsdkIndex();
        } else {
            GatewayManager gatewayManager = SDKManager.getDeviceSDK(setParam.getDeviceSn());
            if (!StringUtils.hasText(gatewayManager.getDroneSn())) {
                throw new RuntimeException("设备不在线");
            }
            Optional<List<PsdkWidget>> dronePsdkValues = psdkWidgetRedisService.getPsdkWidgetValues(gatewayManager.getDroneSn());
            if (dronePsdkValues.isEmpty()) {
                throw new RuntimeException("设备不存在psdk负载");
            }
            Optional<PsdkWidget> speakerOpt = dronePsdkValues.get()
                    .stream()
                    .filter(x -> PsdkNameEnum.SPEAKER == x.getPsdkName()).findFirst();
            if (speakerOpt.isEmpty()) {
                throw new RuntimeException("设备不存在喊话器");
            }
            return speakerOpt.get().getPsdkIndex();
        }
    }

}
