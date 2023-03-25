package com.dji.sample.cloudapi.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 飞行航线类型
 *
 * @author Qfei
 * @date 2023/3/25 20:03
 */
@Getter
@AllArgsConstructor
public enum WaylineType {
    WAYPOINT(0, 3),

    MAPPING_2D(1, 2),

    MAPPING_3D(2, 100),

    MAPPING_STRIP(4, 100),
    Unknown(Integer.MAX_VALUE, 100);

    private final int type;
    private final int flightType;

    public static WaylineType getWaylineType(int type) {
        return Arrays.stream(WaylineType.values())
                .filter(waylineType -> waylineType.getType() == type)
                .findAny()
                .orElse(WaylineType.Unknown);
    }
}
