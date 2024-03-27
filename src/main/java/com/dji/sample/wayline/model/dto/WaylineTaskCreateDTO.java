package com.dji.sample.wayline.model.dto;

import com.dji.sample.wayline.model.enums.WaylineTaskTypeEnum;
import com.dji.sample.wayline.model.enums.WaylineTemplateTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sean
 * @version 1.1
 * @date 2022/6/1
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WaylineTaskCreateDTO {

    private String flightId;

    private WaylineTaskTypeEnum taskType;

    private WaylineTemplateTypeEnum waylineType;

    private Long executeTime;

    private WaylineTaskFileDTO file;

    private Integer rthAltitude;

    private Integer outOfControlAction;

    private Integer exitWaylineWhenRcLost;

    private WaylineTaskReadyConditionDTO readyConditions;

    private WaylineTaskExecutableConditionDTO executableConditions;

    private WaylineTaskBreakPoint breakPoint;

    private SimulateMission simulateMission;
}
