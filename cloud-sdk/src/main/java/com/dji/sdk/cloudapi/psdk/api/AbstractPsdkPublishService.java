package com.dji.sdk.cloudapi.psdk.api;

import com.dji.sdk.annotations.CloudSDKVersion;
import com.dji.sdk.cloudapi.psdk.*;
import com.dji.sdk.config.version.CloudSDKVersionEnum;
import com.dji.sdk.config.version.GatewayManager;
import com.dji.sdk.config.version.GatewayTypeEnum;
import com.dji.sdk.mqtt.services.ServicesPublish;
import com.dji.sdk.mqtt.services.ServicesReplyData;
import com.dji.sdk.mqtt.services.TopicServicesResponse;

import javax.annotation.Resource;

/**
 * 互联互通发布消息服务类
 *
 * @author Qfei
 * @date 2024/8/12 17:26
 */
public abstract class AbstractPsdkPublishService {

    @Resource
    private ServicesPublish servicesPublish;

    /**
     * cloud-custom data transmit to esdk
     * @param gateway   gateway device
     * @return  services_reply
     */
    @CloudSDKVersion(since = CloudSDKVersionEnum.V1_0_0, exclude = GatewayTypeEnum.RC)
    public TopicServicesResponse<ServicesReplyData> customDataTransmissionToEsdk(GatewayManager gateway, CustomDataTransmissionToEsdkRequest request) {
        return servicesPublish.publish(
                gateway.getGatewaySn(),
                PsdkMethodEnum.CUSTOM_DATA_TRANSMISSION_TO_ESDK.getMethod(),
                request);
    }

    /**
     * cloud-custom data transmit to psdk
     * @param gateway   gateway device
     * @return  services_reply
     */
    @CloudSDKVersion(since = CloudSDKVersionEnum.V1_0_0, exclude = GatewayTypeEnum.RC)
    public TopicServicesResponse<ServicesReplyData> customDataTransmissionToPsdk(GatewayManager gateway, CustomDataTransmissionToPsdkRequest request) {
        return servicesPublish.publish(
                gateway.getGatewaySn(),
                PsdkMethodEnum.CUSTOM_DATA_TRANSMISSION_TO_PSDK.getMethod(),
                request);
    }

    @CloudSDKVersion(since = CloudSDKVersionEnum.V1_0_0, exclude = GatewayTypeEnum.RC)
    public TopicServicesResponse<ServicesReplyData> psdkWidgetValueSet(GatewayManager gateway, PSDKWidgetValueSetRequest request) {
        return servicesPublish.publish(
                gateway.getGatewaySn(),
                PsdkMethodEnum.PSDK_WIDGET_VALUE_SET.getMethod(),
                request);
    }

    @CloudSDKVersion(since = CloudSDKVersionEnum.V1_0_0, exclude = GatewayTypeEnum.RC)
    public TopicServicesResponse<ServicesReplyData> psdkInputBoxTextSet(GatewayManager gateway, PSDKInputBoxTextSetRequest request) {
        return servicesPublish.publish(
                gateway.getGatewaySn(),
                PsdkMethodEnum.PSDK_INPUT_BOX_TEXT_SET.getMethod(),
                request);
    }

    @CloudSDKVersion(since = CloudSDKVersionEnum.V1_0_0)
    public TopicServicesResponse<ServicesReplyData> speakerAudioPlayStart(GatewayManager gateway, SpeakerAudioPlayStartRequest request) {
        return servicesPublish.publish(
                gateway.getGatewaySn(),
                PsdkMethodEnum.SPEAKER_AUDIO_PLAY_START.getMethod(),
                request,
                request.getJobId());
    }

    @CloudSDKVersion(since = CloudSDKVersionEnum.V1_0_0, exclude = GatewayTypeEnum.RC)
    public TopicServicesResponse<ServicesReplyData> speakerTtsPlayStart(GatewayManager gateway, SpeakerTtsPlayStartRequest request) {
        return servicesPublish.publish(
                gateway.getGatewaySn(),
                PsdkMethodEnum.SPEAKER_TTS_PLAY_START.getMethod(),
                request,
                request.getJobId());
    }

    @CloudSDKVersion(since = CloudSDKVersionEnum.V1_0_0, exclude = GatewayTypeEnum.RC)
    public TopicServicesResponse<ServicesReplyData> speakerReplay(GatewayManager gateway, SpeakerPlayRequest request) {
        return servicesPublish.publish(
                gateway.getGatewaySn(),
                PsdkMethodEnum.SPEAKER_REPLAY.getMethod(),
                request);
    }

    @CloudSDKVersion(since = CloudSDKVersionEnum.V1_0_0, exclude = GatewayTypeEnum.RC)
    public TopicServicesResponse<ServicesReplyData> speakerPlayStop(GatewayManager gateway, SpeakerPlayRequest request) {
        return servicesPublish.publish(
                gateway.getGatewaySn(),
                PsdkMethodEnum.SPEAKER_PLAY_STOP.getMethod(),
                request);
    }

    @CloudSDKVersion(since = CloudSDKVersionEnum.V1_0_0, exclude = GatewayTypeEnum.RC)
    public TopicServicesResponse<ServicesReplyData> speakerPlayModeSet(GatewayManager gateway, SpeakerPlayModeSetRequest request) {
        return servicesPublish.publish(
                gateway.getGatewaySn(),
                PsdkMethodEnum.SPEAKER_PLAY_MODE_SET.getMethod(),
                request);
    }

    @CloudSDKVersion(since = CloudSDKVersionEnum.V1_0_0, exclude = GatewayTypeEnum.RC)
    public TopicServicesResponse<ServicesReplyData> speakerPlayVolumeSet(GatewayManager gateway, SpeakerPlayVolumeSetRequest request) {
        return servicesPublish.publish(
                gateway.getGatewaySn(),
                PsdkMethodEnum.SPEAKER_PLAY_VOLUME_SET.getMethod(),
                request);
    }
}
