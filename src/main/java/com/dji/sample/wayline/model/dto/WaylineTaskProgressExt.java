package com.dji.sample.wayline.model.dto;

import lombok.Data;

/**
 * @author sean
 * @version 1.1
 * @date 2022/6/9
 */
@Data
public class WaylineTaskProgressExt {

    private Integer currentWaypointIndex;

    private Integer mediaCount;

    private String flightId;

    private String trackId;
}
