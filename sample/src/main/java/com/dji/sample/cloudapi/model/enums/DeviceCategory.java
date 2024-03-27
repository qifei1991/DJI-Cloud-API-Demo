package com.dji.sample.cloudapi.model.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 设备类别
 *
 * @author Qfei
 * @date 2022/11/23 13:49
 */
public enum DeviceCategory {
    /**
     * 设备类别，无人机、机场、手柄等
     */
    AIRCRAFT("aircraft"),
    DOCK("dock"),
    RC("rc");

    @Getter
    private final String code;

    DeviceCategory(String code) {

        this.code = code;
    }

    public static DeviceCategory getCategory(String category) {

        return Arrays.stream(values()).filter(e -> e.getCode().equals(category)).findFirst().orElse(null);
    }
}
