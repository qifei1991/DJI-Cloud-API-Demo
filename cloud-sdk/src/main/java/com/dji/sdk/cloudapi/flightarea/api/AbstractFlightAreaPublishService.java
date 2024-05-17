package com.dji.sdk.cloudapi.flightarea.api;

import com.dji.sdk.annotations.CloudSDKVersion;
import com.dji.sdk.cloudapi.flightarea.FlightAreaMethodEnum;
import com.dji.sdk.config.version.CloudSDKVersionEnum;
import com.dji.sdk.config.version.GatewayManager;
import com.dji.sdk.config.version.GatewayTypeEnum;
import com.dji.sdk.mqtt.services.ServicesPublish;
import com.dji.sdk.mqtt.services.ServicesReplyData;
import com.dji.sdk.mqtt.services.TopicServicesResponse;

import javax.annotation.Resource;

/**
 * @author Qfei
 * @date 2024/5/14 18:59
 */
public abstract class AbstractFlightAreaPublishService {

    @Resource
    private ServicesPublish servicesPublish;

    /**
     * Update command
     * @param gateway   gateway device
     * @return  services_reply
     */
    @CloudSDKVersion(since = CloudSDKVersionEnum.V1_0_0, exclude = GatewayTypeEnum.RC, include = {GatewayTypeEnum.DOCK, GatewayTypeEnum.DOCK2})
    public TopicServicesResponse<ServicesReplyData> flightAreasUpdate(GatewayManager gateway) {
        return servicesPublish.publish(
                gateway.getGatewaySn(),
                FlightAreaMethodEnum.FLIGHT_AREAS_UPDATE.getMethod());
    }
}
