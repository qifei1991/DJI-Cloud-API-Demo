package com.dji.sample.wayline.model.dto;

import com.dji.sdk.annotations.CloudSDKVersion;
import com.dji.sdk.cloudapi.wayline.*;
import com.dji.sdk.config.version.CloudSDKVersionEnum;
import com.dji.sdk.config.version.GatewayTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author sean
 * @version 1.1
 * @date 2022/6/1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WaylineJobDTO {

    private String jobId;

    private String jobName;

    private String fileId;

    private String fileName;

    private String dockSn;

    private String dockName;

    private String workspaceId;

    private WaylineTypeEnum waylineType;

    private TaskTypeEnum taskType;

    private LocalDateTime executeTime;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private LocalDateTime completedTime;

    private Integer status;

    private Integer progress;

    private String username;

    private Integer code;

    private Integer rthAltitude;

    private OutOfControlActionEnum outOfControlAction;

    private Integer mediaCount;

    private Integer uploadedCount;

    private Boolean uploading;

    private WaylineTaskConditionDTO conditions;

    private String parentId;

    private Integer exitWaylineWhenRcLost;

    private String groupId;

    private Boolean continuable;

    private FlighttaskBreakPoint breakPoint;

    /**
     * 模拟飞行属性
     */
    private SimulateMission simulateMission;

    @Builder.Default
    private RthModeEnum rthMode = RthModeEnum.PRESET_HEIGHT;
    private WaylinePrecisionTypeEnum waylinePrecisionType;
}
