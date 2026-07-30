package com.dji.sdk.cloudapi.device;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 固件一致性升级
 * <pre>
 *     一致性升级：指飞行器某些模块的固件版本与系统匹配版本不一致，需要进行升级。
 *          常见的情况例如：飞行器与遥控器已经升级至最新版本，但替换电池时发现电池未升级，此时一致性升级将被提示。
 *     普通升级：开发者将飞行器所有模块升级至指定固件版本。
 * </pre>
 *
 * @author Qfei
 * @date 2026/7/24 15:56
 */
public enum CompatibleStatusEnum {
    /**
     * 固件一致性	enum_int	{"0":"不需要一致性升级","1":"需要一致性升级"}
     */
    NOT_COMPATIBLE(0),
    COMPATIBLE(1);

    private final int code;

    CompatibleStatusEnum(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    @JsonCreator
    public static CompatibleStatusEnum fromCode(int code) {
        return Arrays.stream(values()).filter(status -> status.code == code)
                .findFirst().orElseThrow(() -> new CloudSDKException(CompatibleStatusEnum.class, code));
    }
}
