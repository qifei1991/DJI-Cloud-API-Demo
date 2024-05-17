package com.dji.sample.map.service.impl;

import com.dji.sdk.cloudapi.map.OfflineMapGetRequest;
import com.dji.sdk.cloudapi.map.OfflineMapGetResponse;
import com.dji.sdk.cloudapi.map.OfflineMapSyncProgress;
import com.dji.sdk.cloudapi.map.api.AbstractOfflineMapService;
import com.dji.sdk.cloudapi.property.DockDroneOfflineMapEnable;
import com.dji.sdk.mqtt.MqttReply;
import com.dji.sdk.mqtt.requests.TopicRequestsRequest;
import com.dji.sdk.mqtt.requests.TopicRequestsResponse;
import com.dji.sdk.mqtt.state.TopicStateRequest;
import com.dji.sdk.mqtt.state.TopicStateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

/**
 * @author Qfei
 * @date 2024/5/17 14:26
 */
@Slf4j
@Service
public class SDKOfflineMapService extends AbstractOfflineMapService {
    /**
     * @param request data
     * @return
     */
    @Override
    public TopicStateResponse<MqttReply> dockDroneOfflineMapEnable(TopicStateRequest<DockDroneOfflineMapEnable> request, MessageHeaders headers) {
        log.error("*************** dockDroneOfflineMapEnable not implemented! ***************");
        log.info("- DockDrone Offline map Enable: gateway: {}, data: {}", request.getGateway(), request.getData());

        return new TopicStateResponse<MqttReply>();
    }

    /**
     * @param request data
     * @return
     */
    @Override
    public TopicRequestsResponse<MqttReply> offlineMapSyncProgress(TopicRequestsRequest<OfflineMapSyncProgress> request, MessageHeaders headers) {
        log.error("*************** offlineMapSyncProgress not implemented! ***************");
        log.info("- Offline map sync progress: gateway: {}, data: {}", request.getGateway(), request.getData());

        return new TopicRequestsResponse<MqttReply>();
    }

    /**
     * @param request data
     * @return
     */
    @Override
    public TopicRequestsResponse<MqttReply<OfflineMapGetResponse>> offlineMapGet(TopicRequestsRequest<OfflineMapGetRequest> request, MessageHeaders headers) {
        log.error("*************** offlineMapGet not implemented! ***************");
        log.info("- Offline map Get: gateway: {}, data: {}", request.getGateway(), request.getData());

        return new TopicRequestsResponse<MqttReply<OfflineMapGetResponse>>();
    }
}
