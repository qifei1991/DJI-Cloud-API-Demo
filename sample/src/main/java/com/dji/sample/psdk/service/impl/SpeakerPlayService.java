package com.dji.sample.psdk.service.impl;

import com.dji.sample.psdk.model.param.SpeakerPlaySetParam;
import com.dji.sample.psdk.service.ISpeakerPlayService;
import com.dji.sdk.cloudapi.psdk.SpeakerPlayModeSetRequest;
import com.dji.sdk.cloudapi.psdk.SpeakerPlayVolumeSetRequest;
import com.dji.sdk.common.HttpResultResponse;
import com.dji.sdk.common.SDKManager;
import com.dji.sdk.mqtt.services.ServicesReplyData;
import com.dji.sdk.mqtt.services.TopicServicesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Qfei
 * @date 2024/8/12 18:06
 */
@Service
public class SpeakerPlayService implements ISpeakerPlayService {

    @Autowired
    private SDKPsdkPublishService sdkPsdkPublishService;

    @Override
    public HttpResultResponse setPlayMode(String workspaceId, SpeakerPlaySetParam setParam) {
        if (Objects.isNull(setParam.getMode())) {
            HttpResultResponse.error("播放模式参数错误");
        }

        TopicServicesResponse<ServicesReplyData> serviceReply = sdkPsdkPublishService.speakerPlayModeSet(
                SDKManager.getDeviceSDK(setParam.getDeviceSn()),
                new SpeakerPlayModeSetRequest()
                        .setPsdkIndex(setParam.getPsdkIndex())
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

        TopicServicesResponse<ServicesReplyData> serviceReply = sdkPsdkPublishService.speakerPlayVolumeSet(
                SDKManager.getDeviceSDK(setParam.getDeviceSn()),
                new SpeakerPlayVolumeSetRequest()
                        .setPsdkIndex(setParam.getPsdkIndex())
                        .setPlayVolume(setParam.getVolume()));
        if (!serviceReply.getData().getResult().isSuccess()) {
            return HttpResultResponse.error(serviceReply.getData().getResult().getMessage());
        }
        return HttpResultResponse.success();
    }
}
