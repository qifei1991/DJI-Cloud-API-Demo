package com.dji.sample.wayline.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author sean
 * @version 1.3
 * @date 2022/9/26
 */
@Getter
@AllArgsConstructor
public enum WaylineTemplateTypeEnum {

    WAYPOINT(0, "waypoint"),

    MAPPING_2D(1, "mapping2d"),

    MAPPING_3D(2, "mapping3d"),

    MAPPING_STRIP(4, "mappingStrip");

    final int val;
    final String code;

    public static WaylineTemplateTypeEnum findTemplateType(String code) {
        return Arrays.stream(WaylineTemplateTypeEnum.values())
                .filter(x -> x.getCode().equalsIgnoreCase(code))
                .findAny()
                .orElse(WAYPOINT);
    }
}
