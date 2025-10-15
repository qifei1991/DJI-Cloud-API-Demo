package com.dji.sample.cloudapi.model.param;

import com.dji.sdk.cloudapi.control.Point;
import com.dji.sdk.cloudapi.control.TakeoffStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 一键起飞飞向目标点完成参数
 *
 * @author Qfei
 * @date 2025/8/19 14:09
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TakeoffToProgressParam {

    private String tid;

    private String bid;

    private String dockSn;

    private String droneSn;

    private Integer result;

    private TakeoffStatusEnum status;

    private String flightId;

    private String trackId;

    private Integer wayPointIndex;

    /**
     * Remaining mission distance
     * unit: m
     */
    private Float remainingDistance;

    /**
     * Remaining mission time
     * unit: s
     */
    private Integer remainingTime;

    /**
     * Planned trajectory point list
     */
    private List<Point> plannedPathPoints;
}
