package com.dji.sample.cloudapi.model.param;

import lombok.Builder;
import lombok.Data;

/**
 * 手柄设备状态信息
 *
 * @author Qfei
 * @date 2022/11/23 14:39
 */
@Data
@Builder
public class RcOsdParam {
    /**
     * 飞行记录ID（架次ID）
     */
    private String sortiesId;
    private String sn;
    private String firmwareVersion;
    private Double longitude;
    private Double latitude;
    /**
     * 剩余点了百分比
     */
    private Integer batteryPercent;
    /**
     * 信息推送模式，{"0":"定频", "1":"不定频，但信息发生变化做主动推送"}
     */
    private Integer pushMode;
    private Long time;
}
