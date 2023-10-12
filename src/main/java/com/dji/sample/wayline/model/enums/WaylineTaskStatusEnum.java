package com.dji.sample.wayline.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author sean
 * @version 1.3
 * @date 2023/2/1
 */
public enum WaylineTaskStatusEnum {

    /**
     * 暂停
     */
    PAUSE,
    /**
     * 恢复
     */
    RESUME,
    /**
     * 断点续飞
     * modify by Qfei at 2023-10-10 14:40:39
     */
    BREAK_POINT_CONTINUE;

    @JsonValue
    public int getVal() {
        return ordinal();
    }

}
