package com.dji.sample.interconnection.service.impl;

import com.dji.sdk.cloudapi.interconnection.CustomDataTransmissionFromEsdk;
import com.dji.sdk.cloudapi.interconnection.api.AbstractInterconnectionService;
import com.dji.sdk.mqtt.MqttReply;
import com.dji.sdk.mqtt.events.TopicEventsRequest;
import com.dji.sdk.mqtt.events.TopicEventsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

/**
 * @author Qfei
 * @date 2024/4/23 16:56
 */
@Slf4j
@Service
public class SDKInterconnectionService extends AbstractInterconnectionService {

    /**
     * cloud-custom data transmit from psdk
     *
     * @param request data
     * @param headers The headers for a {@link org.springframework.messaging.Message}.
     * @return events_reply
     */
    @Override
    public TopicEventsResponse<MqttReply> customDataTransmissionFromPsdk(TopicEventsRequest<CustomDataTransmissionFromEsdk> request, MessageHeaders headers) {

        log.info("CustomDataTransmissionFromPsdk: gateway: {}, data: {}", request.getFrom(), request.getData());

        return new TopicEventsResponse<>();
    }
}
