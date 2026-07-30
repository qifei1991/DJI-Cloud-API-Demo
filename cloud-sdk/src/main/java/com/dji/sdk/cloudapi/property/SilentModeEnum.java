package com.dji.sdk.cloudapi.property;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * <pre>
 * {"0":"非静音模式","1":"静音模式"}
 * 开启静音模式，意味着：
 *   1. 风扇转速降低、空调制冷性能下降、炎热天气下作业间隔变长。
 *   2. 蜂鸣器声音将关闭，开关舱盖时请注意周围安全。
 *   3. 机场待机状态的白色指示灯关闭，其他运行状态的指示灯正常。
 * </pre>
 * @author sean
 * @date 2023/12/12
 * @version 1.9
 */
public enum SilentModeEnum {

    RING(0),

    SILENT(1),

    ;

    private final int mode;

    SilentModeEnum(int mode) {
        this.mode = mode;
    }

    @JsonValue
    public int getMode() {
        return mode;
    }

    @JsonCreator
    public static SilentModeEnum find(int mode) {
        return Arrays.stream(values()).filter(modeEnum -> modeEnum.mode == mode).findAny()
            .orElseThrow(() -> new CloudSDKException(SilentModeEnum.class, mode));
    }

}
