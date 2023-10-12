package com.dji.sample.wayline.model.dto;

import com.dji.sample.wayline.model.enums.BreakPointStateEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

/**
 * 创建飞行任务断点信息
 *
 * @author Qfei
 * @date 2023/10/10 12:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaylineTaskBreakPoint {

    /**
     * 断点序号
     */
    private Integer index;

    /**
     * 断点状态
     */
    private BreakPointStateEnum state;

    /**
     * 当前航段进度
     */
    @Range(min = 0, max = 1)
    private Float progress;

    /**
     * 航线ID
     */
    private Integer waylineId;
}
