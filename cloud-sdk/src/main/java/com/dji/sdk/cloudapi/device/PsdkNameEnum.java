package com.dji.sdk.cloudapi.device;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * psdk名称枚举
 *
 * @author Qfei
 * @date 2025/11/27 16:41
 */
public enum PsdkNameEnum {

    SEARCH_LIGHT("Searchlight", " 探照灯"),

    SPEAKER("Speaker", "喊话器")
    ;

    private final String name;

    private final String desc;

    PsdkNameEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    @JsonCreator
    public static PsdkNameEnum find(String name) {
        return Arrays.stream(values())
                .filter(x -> x.name.equals(name))
                .findFirst()
                .orElseThrow(() -> new CloudSDKException(PsdkNameEnum.class, name));
    }
}
