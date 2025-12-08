package com.dji.sample.cloudapi.model.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 设备类别
 *
 * @author Qfei
 * @date 2022/11/23 13:49
 */
@Getter
public enum DeviceCategory {
    /**
     * 设备类别，无人机、机场、手柄等
     */
    AIRCRAFT("aircraft", 0),

    DOCK("dock", 3),

    RC("rc", 2);

    private final String code;

    private final int domain;

    DeviceCategory(String code, int domain) {
        this.code = code;
        this.domain = domain;
    }

    public static DeviceCategory getCategory(String category) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(category))
                .findFirst()
                .orElse(null);
    }

    public static DeviceCategory getCategory(int domain) {
        return Arrays.stream(values())
                .filter(e -> e.getDomain() == domain)
                .findFirst()
                .orElse(null);
    }
}
