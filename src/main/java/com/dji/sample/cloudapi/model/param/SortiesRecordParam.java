package com.dji.sample.cloudapi.model.param;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 架次开始飞行参数
 *
 * @author Qfei
 * @date 2022/11/23 15:55
 */
@Data
@Builder
public class SortiesRecordParam {
    @NotBlank(message = "架次ID不能为空")
    private String sortiesId;
    private String name;
    private String aircraftSn;
    private Integer flightType;
    private Integer times;
    /**
     * 飞行时长
     */
    private Double duration;
    /**
     * 飞行高度
     */
    private Double peekHeight;
    private String startTime;
    private String endTime;
    /**
     * 飞行状态，{"0":"未飞行", "1":"飞行中", "3":"飞行结束"}
     */
    private Integer state;
    /**
     * 媒体文件数量
     */
    private Integer fileTotal;
    /**
     * 航线ID
     */
    private String waylineId;

    private String flightTrackWkt;
}
