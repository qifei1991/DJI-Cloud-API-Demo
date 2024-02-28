package com.dji.sample.manage.model.param;

import cn.hutool.core.text.StrPool;
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

    private Set<String> deviceSn;

    private Long beginTime;

    private Long endTime;

    private String language;

    private String message;

    private Long page;

    private Long pageSize;

    private Integer level;

    private Long updateTime;

    /**
     * 接收多个设备SN
     * @param deviceSnStr
     */
    public void setDeviceSn(String deviceSnStr) {
        this.deviceSn = new HashSet<>(Set.of(deviceSnStr.split(StrPool.COMMA)));
    }
}
