package com.dji.sample.wayline.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 航线断点状态
 *
 * @author Qfei
 * @date 2023/10/10 14:04
 */
public enum BreakPointStateEnum {

    /**
     * 航段
     */
    WAYLINE_SEGMENT,
    /**
     * 航点
     */
    WAYLINE_POINT;

    @JsonValue
    public int getVal() {
        return ordinal();
    }
}
