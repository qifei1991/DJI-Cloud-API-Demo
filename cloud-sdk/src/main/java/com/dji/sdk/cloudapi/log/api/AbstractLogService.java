package com.dji.sdk.cloudapi.log.api;

import com.dji.sdk.cloudapi.log.FileUploadProgress;
import com.dji.sdk.mqtt.ChannelName;
import com.dji.sdk.mqtt.MqttReply;
import com.dji.sdk.mqtt.events.EventsDataRequest;
import com.dji.sdk.mqtt.events.TopicEventsRequest;
import com.dji.sdk.mqtt.events.TopicEventsResponse;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

/**
 * @author sean
 * @version 1.7
 * @date 2023/6/28
 */
public abstract class AbstractLogService {

    /**
     * Inform of file uploading progress
     * @param request  data
     * @param headers   The headers for a {@link Message}.
     * @return events_reply
     */
    @ServiceActivator(inputChannel = ChannelName.INBOUND_EVENTS_FILEUPLOAD_PROGRESS, outputChannel = ChannelName.OUTBOUND_EVENTS)
    public TopicEventsResponse<MqttReply> fileuploadProgress(TopicEventsRequest<EventsDataRequest<FileUploadProgress>> request, MessageHeaders headers) {
        throw new UnsupportedOperationException("fileuploadProgress not implemented");
    }

}
