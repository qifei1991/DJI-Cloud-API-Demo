package com.dji.sample.manage.model.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 *
 * @author sean.zhou
 * @date 2021/11/15
 * @version 0.1
 */
@Getter
public enum DeviceDomainEnum {

    SUB_DEVICE(0),

    GATEWAY(2),

    PAYLOAD(1),

    DOCK (3);

    int val;

    DeviceDomainEnum(int val) {
        this.val = val;
    }


    public static DeviceDomainEnum getDeviceDomain(int val) {
        return Arrays.stream(values()).filter(e -> e.getVal() == val).findFirst().orElse(null);
    }
}
