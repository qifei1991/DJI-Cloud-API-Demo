package com.dji.sample.airsense.service.impl;

import com.dji.sample.airsense.service.IAirSenseService;
import com.dji.sdk.cloudapi.airsense.AirsenseWarning;
import com.dji.sdk.cloudapi.airsense.api.AbstractAirsenseService;
import com.dji.sdk.mqtt.MqttReply;
import com.dji.sdk.mqtt.events.TopicEventsRequest;
import com.dji.sdk.mqtt.events.TopicEventsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Qfei
 * @date 2026/1/16 19:00
 */
@Slf4j
@Service
public class AirSenseServiceImpl extends AbstractAirsenseService implements IAirSenseService {

    @Override
    public TopicEventsResponse<MqttReply> airsenseWarning(TopicEventsRequest<List<AirsenseWarning>> request, MessageHeaders headers) {
        log.error("*************** airsenseWarning not implemented! ***************");
        log.info("- AirSenseWarning: Gateway: {}, From: {}, Data: {}", request.getGateway(), request.getFrom(), request.getData());
        return new TopicEventsResponse<>();
    }
}
