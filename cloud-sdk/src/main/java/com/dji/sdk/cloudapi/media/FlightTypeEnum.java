package com.dji.sdk.cloudapi.media;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 飞行类型
 *
 * @author Qfei
 * @date 2025/5/8 14:04
 */
public enum FlightTypeEnum {

    /**
     * 飞行类型枚举值
     * {"0":"航线任务","1":"一键起飞任务"}
     */
    WAYLINE_TASK(0),

    takeoff_task(1),

    ;

    private final int type;

    FlightTypeEnum(int type) {
        this.type = type;
    }

    @JsonValue
    public int getType() {
        return type;
    }

    @JsonCreator
    public static FlightTypeEnum find(int type) {
        return Arrays.stream(values())
                .filter(typeEnum -> typeEnum.type == type)
                .findFirst()
                .orElseThrow(() -> new CloudSDKException(FlightTypeEnum.class, type));
    }
}
