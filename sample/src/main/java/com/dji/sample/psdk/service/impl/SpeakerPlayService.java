package com.dji.sample.psdk.service.impl;

import com.dji.sample.psdk.model.param.SpeakerPlaySetParam;
import com.dji.sample.psdk.service.IPsdkService;
import com.dji.sample.psdk.service.ISpeakerPlayService;
import com.dji.sdk.cloudapi.psdk.SpeakerPlayModeSetRequest;
import com.dji.sdk.cloudapi.psdk.SpeakerPlayVolumeSetRequest;
import com.dji.sdk.common.HttpResultResponse;
import com.dji.sdk.mqtt.services.ServicesReplyData;
import com.dji.sdk.mqtt.services.TopicServicesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Qfei
 * @date 2024/8/12 18:06
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SpeakerPlayService implements ISpeakerPlayService {

    private final SDKPsdkPublishService sdkPsdkPublishService;
    private final IPsdkService psdkService;

    @Override
    public HttpResultResponse setPlayMode(String workspaceId, SpeakerPlaySetParam setParam) {
        if (Objects.isNull(setParam.getMode())) {
            HttpResultResponse.error("播放模式参数错误");
        }
        Integer psdkIndex = psdkService.getSetParamPsdkIndex(setParam);
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
        Integer psdkIndex = psdkService.getSetParamPsdkIndex(setParam);
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

}
