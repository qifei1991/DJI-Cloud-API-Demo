package com.dji.sdk.cloudapi.storage.api;

import com.dji.sdk.cloudapi.media.StorageConfigGet;
import com.dji.sdk.cloudapi.storage.StsCredentialsResponse;
import com.dji.sdk.mqtt.ChannelName;
import com.dji.sdk.mqtt.MqttReply;
import com.dji.sdk.mqtt.requests.TopicRequestsRequest;
import com.dji.sdk.mqtt.requests.TopicRequestsResponse;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHeaders;

/**
 * @author Qfei
 * @date 2024/5/14 18:54
 */
public abstract class AbstractStorageService {

    /**
     * Obtain upload temporary credentials
     * @param request
     * @param headers
     */
    @ServiceActivator(inputChannel = ChannelName.INBOUND_REQUESTS_STORAGE_CONFIG_GET, outputChannel = ChannelName.OUTBOUND_REQUESTS)
    public TopicRequestsResponse<MqttReply<StsCredentialsResponse>> storageConfigGet(TopicRequestsRequest<StorageConfigGet> request, MessageHeaders headers) {
        throw new UnsupportedOperationException("storageConfigGet not implemented");
    }

}
