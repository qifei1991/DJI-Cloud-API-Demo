package com.dji.sample.psdk.service.impl;

import com.dji.sdk.mqtt.IMqttTopicService;
import com.dji.sdk.mqtt.events.EventsSubscribe;
import com.dji.sdk.mqtt.property.PropertySetSubscribe;
import com.dji.sdk.mqtt.requests.RequestsSubscribe;
import com.dji.sdk.mqtt.services.ServicesSubscribe;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 移动端飞控喊话器功能服务类
 *
 * @author Qfei
 * @date 2025/12/17 17:00
 */
@Component
public class MzPilotSpeakerService {

    @Resource
    private IMqttTopicService topicService;

    /**
     * @param deviceSn
     */
    public void subscribe(String deviceSn) {
        // topicService.subscribe(String.format(StatusSubscribe.TOPIC, deviceSn));
        // topicService.subscribe(String.format(StateSubscribe.TOPIC, deviceSn));
        // topicService.subscribe(String.format(OsdSubscribe.TOPIC, deviceSn));
        topicService.subscribe(String.format(ServicesSubscribe.TOPIC, deviceSn));
        topicService.subscribe(String.format(EventsSubscribe.TOPIC, deviceSn));
        topicService.subscribe(String.format(RequestsSubscribe.TOPIC, deviceSn));
        topicService.subscribe(String.format(PropertySetSubscribe.TOPIC, deviceSn));
    }

    /**
     * 判断是否是Drone设备
     * @param deviceSn 设备SN
     */
    public boolean isDroneSn(String deviceSn) {
        return StringUtils.hasText(deviceSn) && deviceSn.length() > 14;
    }
}
