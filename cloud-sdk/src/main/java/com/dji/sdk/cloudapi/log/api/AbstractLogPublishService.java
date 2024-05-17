package com.dji.sdk.cloudapi.log.api;

import com.dji.sdk.cloudapi.log.*;
import com.dji.sdk.config.version.GatewayManager;
import com.dji.sdk.mqtt.services.ServicesPublish;
import com.dji.sdk.mqtt.services.ServicesReplyData;
import com.dji.sdk.mqtt.services.TopicServicesResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.annotation.Resource;

/**
 * @author Qfei
 * @date 2024/5/14 19:16
 */
public class AbstractLogPublishService {

    @Resource
    private ServicesPublish servicesPublish;

    /**
     * Get file list of uploadable device
     * @param gateway
     * @param request   data
     * @return  services_reply
     */
    public TopicServicesResponse<ServicesReplyData<FileUploadListResponse>> fileuploadList(GatewayManager gateway, FileUploadListRequest request) {
        return servicesPublish.publish(
                new TypeReference<FileUploadListResponse>() {},
                gateway.getGatewaySn(),
                LogMethodEnum.FILE_UPLOAD_LIST.getMethod(),
                request);
    }

    /**
     * Start the log file uploading
     * @param gateway
     * @param request   data
     * @return  services_reply
     */
    public TopicServicesResponse<ServicesReplyData> fileuploadStart(GatewayManager gateway, FileUploadStartRequest request) {
        return servicesPublish.publish(
                gateway.getGatewaySn(),
                LogMethodEnum.FILE_UPLOAD_START.getMethod(),
                request);
    }

    /**
     * Update the uploding state
     * @param gateway
     * @param request   data
     * @return  services_reply
     */
    public TopicServicesResponse<ServicesReplyData> fileuploadUpdate(GatewayManager gateway, FileUploadUpdateRequest request) {
        return servicesPublish.publish(
                gateway.getGatewaySn(),
                LogMethodEnum.FILE_UPLOAD_UPDATE.getMethod(),
                request);
    }
}
