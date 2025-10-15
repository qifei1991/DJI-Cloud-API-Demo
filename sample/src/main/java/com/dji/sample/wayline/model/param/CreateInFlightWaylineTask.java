package com.dji.sample.wayline.model.param;

import com.dji.sdk.cloudapi.wayline.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建空中航线任务参数
 *
 * @author Qfei
 * @date 2025/8/16 17:28
 */
@Data
@NoArgsConstructor
public class CreateInFlightWaylineTask {

    @NotBlank
    private String dockSn;

    @NotBlank
    private String fileId;

    @Range(min = 20, max = 10000)
    @NotNull
    private Integer rthAltitude;

    @NotNull
    private OutOfControlActionEnum outOfControlAction;

    @Deprecated
    @NotNull
    @Range(max = 2)
    private Integer exitWaylineWhenRcLost;

    private String username;

    private RthModeEnum rthMode = RthModeEnum.PRESET_HEIGHT;

    private WaylinePrecisionTypeEnum waylinePrecisionType = WaylinePrecisionTypeEnum.RTK;

    private String workspaceId;

    private String inFlightWaylineId;
}
