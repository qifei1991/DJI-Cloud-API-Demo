package com.dji.sdk.cloudapi.control;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * @author Qfei
 * @date 2026/3/5 18:17
 */
public enum CameraDenoiseLevelEnum {
    /**
     * {"2":"增强降噪 15fps","3":"超强降噪 5fps"}
     */
    ENHANCED_NOISE_REDUCTION(2),

    SUPER_NOISE_REDUCTION(3);

    private final int level;

    CameraDenoiseLevelEnum(int level) {
        this.level = level;
    }

    @JsonValue
    public int getLevel() {
        return level;
    }

    public static CameraDenoiseLevelEnum find(int level) {
        return Arrays.stream(values()).filter(valueEnum -> valueEnum.level == level).findAny()
                .orElseThrow(() -> new CloudSDKException(CameraDenoiseLevelEnum.class, level));
    }
}
