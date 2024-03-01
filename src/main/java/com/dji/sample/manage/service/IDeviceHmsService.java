package com.dji.sample.manage.service;

import com.dji.sample.common.model.PaginationData;
import com.dji.sample.component.mqtt.model.CommonTopicReceiver;
import com.dji.sample.manage.model.dto.DeviceHmsDTO;
import com.dji.sample.manage.model.param.DeviceHmsQueryParam;
import org.springframework.messaging.MessageHeaders;

import java.util.Set;

/**
 * @author sean
 * @version 1.1
 * @date 2022/7/6
 */
public interface IDeviceHmsService {

    /**
     * Handle hms messages.
     * @param receiver
     * @param headers
     */
    void handleHms(CommonTopicReceiver receiver, MessageHeaders headers);

    /**
     * Query hms data by paging according to query parameters.
     * @param param
     * @return
     */
    PaginationData<DeviceHmsDTO> getDeviceHmsByParam(DeviceHmsQueryParam param);

    /**
     * Read message handling.
     * @param deviceSn
     */
    void updateUnreadHms(String deviceSn);

    /**
     * 根据设备SN和Hms标记为已读
     * @param deviceSn 设备SN
     * @param hmsKey 告警信息key集合
     */
    void updateUnreadHmsByHmsKey(String deviceSn, Set<String> hmsKey);
}
