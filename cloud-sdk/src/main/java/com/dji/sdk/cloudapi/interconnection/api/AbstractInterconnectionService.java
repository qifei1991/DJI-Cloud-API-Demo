package com.dji.sdk.cloudapi.interconnection.api;

import com.dji.sdk.annotations.CloudSDKVersion;
import com.dji.sdk.cloudapi.interconnection.CustomDataTransmissionFromEsdk;
import com.dji.sdk.cloudapi.interconnection.SpeakerPlayTaskNotify;
import com.dji.sdk.config.version.CloudSDKVersionEnum;
import com.dji.sdk.mqtt.ChannelName;
import com.dji.sdk.mqtt.MqttReply;
import com.dji.sdk.mqtt.events.EventsDataRequest;
import com.dji.sdk.mqtt.events.TopicEventsRequest;
import com.dji.sdk.mqtt.events.TopicEventsResponse;
import com.dji.sdk.mqtt.services.ServicesPublish;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import javax.annotation.Resource;

/**
 * @author sean
 * @version 1.7
 * @date 2023/10/16
 */
public abstract class AbstractInterconnectionService {

    @Resource
    private ServicesPublish servicesPublish;

    /**
     * cloud-custom data transmit from esdk
     * @param request  data
     * @param headers  The headers for a {@link Message}.
     * @return events_reply
     */
    @ServiceActivator(inputChannel = ChannelName.INBOUND_EVENTS_CUSTOM_DATA_TRANSMISSION_FROM_ESDK, outputChannel = ChannelName.OUTBOUND_EVENTS)
    @CloudSDKVersion(since = CloudSDKVersionEnum.V1_0_0)
    public TopicEventsResponse<MqttReply> customDataTransmissionFromEsdk(TopicEventsRequest<CustomDataTransmissionFromEsdk> request, MessageHeaders headers) {
        throw new UnsupportedOperationException("customDataTransmissionFromEsdk not implemented");
    }

    /**
     * cloud-custom data transmit from psdk
     * @param request  data
     * @param headers  The headers for a {@link Message}.
     * @return events_reply
     */
    @ServiceActivator(inputChannel = ChannelName.INBOUND_EVENTS_CUSTOM_DATA_TRANSMISSION_FROM_PSDK, outputChannel = ChannelName.OUTBOUND_EVENTS)
    @CloudSDKVersion(since = CloudSDKVersionEnum.V1_0_0)
    public TopicEventsResponse<MqttReply> customDataTransmissionFromPsdk(TopicEventsRequest<CustomDataTransmissionFromEsdk> request, MessageHeaders headers) {
        throw new UnsupportedOperationException("customDataTransmissionFromPsdk not implemented");
    }

    @ServiceActivator(inputChannel = ChannelName.INBOUND_EVENTS_SPEAKER_PLAY_STATUS_NOTIFY, outputChannel = ChannelName.OUTBOUND_EVENTS)
    public TopicEventsResponse<MqttReply> speakerPlayTaskStatusNotify(
            TopicEventsRequest<EventsDataRequest<SpeakerPlayTaskNotify>> request, MessageHeaders headers) {
        throw new UnsupportedOperationException("speakerPlayTaskStatusNotify not implemented.");
    }

}
