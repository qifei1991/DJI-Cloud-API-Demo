package com.dji.sdk.cloudapi.device;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 热增益模式
 * @author sean
 * @version 1.7
 * @date 2023/6/30
 */
public enum ThermalGainModeEnum {

    /**
     * {"0":"自动","1":"低增益, 测温范围0°C-500°C","2":"高增益, 测温范围-20°C-150°C", "3":"超清"}
     * 上云API无法设置 0-自动
     */

    AUTOMATIC(0),

    LOW(1),

    HIGH(2),

    SUPER(3),
    ;

    private final int mode;

    ThermalGainModeEnum(int mode) {
        this.mode = mode;
    }

    @JsonValue
    public int getMode() {
        return mode;
    }

    @JsonCreator
    public static ThermalGainModeEnum find(int mode) {
        return Arrays.stream(values()).filter(modeEnum -> modeEnum.mode == mode).findAny()
            .orElseThrow(() -> new CloudSDKException(ThermalGainModeEnum.class, mode));
    }

}
