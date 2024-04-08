package com.dji.sample.cloudapi.model.param;

import lombok.Builder;
import lombok.Data;

/**
 * 设备上线参数
 *
 * @author Qfei
 * @date 2022/11/23 10:04
 */
@Data
@Builder
public class DeviceOnlineParam {
    private String sn;
    private String name;
    /**
     * 类别：aircraft、dock、rc
     */
    private String category;
    /**
     * 每种设备的不同类型，无人机的不同型号、手柄的多种型号等
     */
    private String type;
    private String firmwareVersion;
    /**
     * 上线时间, yyyy-MM-dd HH:mm:ss
     */
    private String time;

    private Long orgId;
    private String orgCode;
    private String userId;
    private String userName;
    private Double longitude;
    private Double latitude;
}
