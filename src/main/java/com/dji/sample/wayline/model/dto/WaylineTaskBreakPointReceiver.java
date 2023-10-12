package com.dji.sample.wayline.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 航线任务进度断点信息
 *
 * @author Qfei
 * @date 2023/10/10 13:56
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WaylineTaskBreakPointReceiver extends WaylineTaskBreakPoint {

    /**
     * 中断原因
     */
    private Integer breakReason;

    /**
     * 断点经度
     */
    private Double longitude;

    /**
     * 断点纬度
     */
    private Double latitude;

    /**
     * 断点偏航轴角度，偏航轴角度与真北角（经线）的角度，0到6点钟方向为正值，6到12点钟方向为负值
     */
    private Float attitudeHead;

    /**
     * 断点相对地球椭球面高度
     */
    private Float height;
}
