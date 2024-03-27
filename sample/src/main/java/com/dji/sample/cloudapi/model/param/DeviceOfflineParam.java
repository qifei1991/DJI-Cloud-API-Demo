package com.dji.sample.cloudapi.model.param;

import lombok.Builder;
import lombok.Data;

/**
 * 设备下线参数
 *
 * @author Qfei
 * @date 2022/11/23 11:57
 */
@Data
@Builder
public class DeviceOfflineParam {
    private String sn;
    @Builder.Default
    private String causeCode = "1";
    /**
     * 下线时间, yyyy-MM-dd HH:mm:ss
     */
    private String time;
}
