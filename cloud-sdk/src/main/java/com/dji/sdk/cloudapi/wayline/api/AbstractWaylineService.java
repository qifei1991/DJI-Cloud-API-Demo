package com.dji.sdk.cloudapi.wayline.api;

import com.dji.sdk.annotations.CloudSDKVersion;
import com.dji.sdk.cloudapi.wayline.*;
import com.dji.sdk.config.version.CloudSDKVersionEnum;
import com.dji.sdk.mqtt.ChannelName;
import com.dji.sdk.mqtt.MqttReply;
import com.dji.sdk.mqtt.events.EventsDataRequest;
import com.dji.sdk.mqtt.events.TopicEventsRequest;
import com.dji.sdk.mqtt.events.TopicEventsResponse;
import com.dji.sdk.mqtt.requests.TopicRequestsRequest;
import com.dji.sdk.mqtt.requests.TopicRequestsResponse;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

/**
 * @author sean
 * @version 1.7
 * @date 2023/5/19
 */
public abstract class AbstractWaylineService {

    /**
     * Notification of device exits the Return to Home (RTH) state
     * @param request  data
     * @param headers   The headers for a {@link Message}.
     * @return events_reply
     */
    @ServiceActivator(inputChannel = ChannelName.INBOUND_EVENTS_DEVICE_EXIT_HOMING_NOTIFY, outputChannel = ChannelName.OUTBOUND_EVENTS)
    public TopicEventsResponse<MqttReply> deviceExitHomingNotify(TopicEventsRequest<DeviceExitHomingNotify> request, MessageHeaders headers) {
        throw new UnsupportedOperationException("deviceExitHomingNotify not implemented");
    }

    /**
     * Report wayline task progress
     * @param request  data
     * @param headers   The headers for a {@link Message}.
     * @return events_reply
     */
    @ServiceActivator(inputChannel = ChannelName.INBOUND_EVENTS_FLIGHTTASK_PROGRESS, outputChannel = ChannelName.OUTBOUND_EVENTS)
    public TopicEventsResponse<MqttReply> flighttaskProgress(TopicEventsRequest<EventsDataRequest<FlighttaskProgress>> request, MessageHeaders headers) {
        throw new UnsupportedOperationException("flighttaskProgress not implemented");
    }

    /**
     * Notification of task readiness
     * @param request  data
     * @param headers   The headers for a {@link Message}.
     * @return events_reply
     */
    @ServiceActivator(inputChannel = ChannelName.INBOUND_EVENTS_FLIGHTTASK_READY, outputChannel = ChannelName.OUTBOUND_EVENTS)
    public TopicEventsResponse<MqttReply> flighttaskReady(TopicEventsRequest<FlighttaskReady> request, MessageHeaders headers) {
        throw new UnsupportedOperationException("flighttaskReady not implemented");
    }

    /**
     * Get the wayline task resource
     * @param request  data
     * @param headers   The headers for a {@link Message}.
     * @return events_reply
     */
    @ServiceActivator(inputChannel = ChannelName.INBOUND_REQUESTS_FLIGHTTASK_RESOURCE_GET, outputChannel = ChannelName.OUTBOUND_REQUESTS)
    public TopicRequestsResponse<MqttReply<FlighttaskResourceGetResponse>> flighttaskResourceGet(TopicRequestsRequest<FlighttaskResourceGetRequest> request, MessageHeaders headers) {
        throw new UnsupportedOperationException("flighttaskResourceGet not implemented");
    }

    /**
     * Return-to-home information
     * @param request  data
     * @param headers   The headers for a {@link Message}.
     * @return events_reply
     */
    @ServiceActivator(inputChannel = ChannelName.INBOUND_EVENTS_RETURN_HOME_INFO, outputChannel = ChannelName.OUTBOUND_EVENTS)
    @CloudSDKVersion(since = CloudSDKVersionEnum.V1_0_0)
    public TopicEventsResponse<MqttReply> returnHomeInfo(TopicEventsRequest<ReturnHomeInfo> request, MessageHeaders headers) {
        throw new UnsupportedOperationException("returnHomeInfo not implemented");
    }

}
