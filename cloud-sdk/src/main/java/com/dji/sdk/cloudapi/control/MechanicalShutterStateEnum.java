package com.dji.sdk.cloudapi.control;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * @author Qfei
 * @date 2026/3/5 16:26
 */
public enum MechanicalShutterStateEnum {
    /**
     * {"0":"关闭","1":"开启"}
     */
    OFF(0, "关闭"),

    ON(1, "开启");

    private final int state;

    private final String desc;

    MechanicalShutterStateEnum(int state, String desc) {
        this.state = state;
        this.desc = desc;
    }

    @JsonValue
    public int getState() {
        return state;
    }

    public String getDesc() {
        return desc;
    }

    @JsonCreator
    public static MechanicalShutterStateEnum find(int value) {
        return Arrays.stream(values()).filter(valueEnum -> valueEnum.state == value).findAny()
                .orElseThrow(() -> new CloudSDKException(MechanicalShutterStateEnum.class, value));
    }

}
