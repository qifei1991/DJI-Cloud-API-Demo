package com.dji.sample.manage.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Qfei
 * @date 2026/5/21 11:26
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "manage_dock_settings")
public class DockSetEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(value = "device_sn")
    private String deviceSn;

    @TableField(value = "workspace_id")
    private String workspaceId;

    @TableField(value = "wind_speed")
    private Integer windSpeed;

    @TableField(value = "rainfall")
    private Integer rainfall;

    @TableField(value = "drone_lost_report_phone")
    private String droneLostReportPhone;

    @TableField(value = "remaining_power_for_return_home")
    private Integer remainingPowerForReturnHome;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Long createTime;

    @TableField("create_username")
    private String createUsername;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

    @TableField("update_username")
    private String updateUsername;
}
