package com.dji.sample.manage.model.param;

import cn.hutool.core.text.StrPool;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author sean
 * @version 1.1
 * @date 2022/7/8
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceHmsQueryParam {

    @JsonProperty("device_sn")
    private Set<String> deviceSn;

    @JsonProperty("begin_time")
    private Long beginTime;

    @JsonProperty("end_time")
    private Long endTime;

    private String language;

    private String message;

    private Long page;

    @JsonProperty("page_size")
    private Long pageSize;

    private Integer level;

    @JsonProperty("update_time")
    private Long updateTime;

    /**
     * 接收多个设备SN
     * @param deviceSnStr
     */
    public void setDeviceSn(String deviceSnStr) {
        this.deviceSn = new HashSet<>(Set.of(deviceSnStr.split(StrPool.COMMA)));
    }
}
