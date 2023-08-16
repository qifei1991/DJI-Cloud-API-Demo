package com.dji.sample.storage.service.impl;

import com.dji.sample.component.mqtt.model.*;
import com.dji.sample.component.mqtt.service.IMessageSenderService;
import com.dji.sample.component.oss.model.OssConfiguration;
import com.dji.sample.component.oss.service.impl.OssServiceContext;
import com.dji.sample.media.model.StsCredentialsDTO;
import com.dji.sample.storage.service.IStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author sean
 * @version 0.3
 * @date 2022/3/9
 */
@Slf4j
@Service
public class StorageServiceImpl implements IStorageService {

    @Autowired
    private IMessageSenderService messageSender;

    @Autowired
    private OssServiceContext ossService;

    @Override
    public StsCredentialsDTO getSTSCredentials() {
        return StsCredentialsDTO.builder()
                .endpoint(StringUtils.hasText(OssConfiguration.extranetEndpoint) ? OssConfiguration.extranetEndpoint : OssConfiguration.endpoint)
                .bucket(OssConfiguration.bucket)
                .credentials(ossService.getCredentials())
                .provider(OssConfiguration.provider)
                .objectKeyPrefix(OssConfiguration.objectDirPrefix)
                .region(OssConfiguration.region)
                .build();
    }

    @Override
    @ServiceActivator(inputChannel = ChannelName.INBOUND_REQUESTS_STORAGE_CONFIG_GET, outputChannel = ChannelName.OUTBOUND)
    public void replyConfigGet(CommonTopicReceiver receiver, MessageHeaders headers) {
        CommonTopicResponse<RequestsReply> response = CommonTopicResponse.<RequestsReply>builder()
                .tid(receiver.getTid())
                .bid(receiver.getBid())
                .data(RequestsReply.success(this.getSTSCredentials()))
                .timestamp(System.currentTimeMillis())
                .method(receiver.getMethod())
                .build();
        messageSender.publish(headers.get(MqttHeaders.RECEIVED_TOPIC) + TopicConst._REPLY_SUF, response);
    }
}
