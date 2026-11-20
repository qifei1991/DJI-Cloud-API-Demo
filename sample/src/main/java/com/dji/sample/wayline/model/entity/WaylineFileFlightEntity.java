package com.dji.sample.wayline.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * The wayline file copy which is used by the flight task.
 * The lost action in the copy is modified by the parameters of the flight task.
 * @author sean
 * @version 1.0
 * @date 2026/11/20
 */
@Data
@TableName("wayline_file_flight")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WaylineFileFlightEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("file_id")
    private String fileId;

    /**
     * It is the wayline_id in the wayline_file table.
     */
    @TableField("original_file_id")
    private String originalFileId;

    @TableField("job_id")
    private String jobId;

    @TableField("object_key")
    private String objectKey;

    @TableField("sign")
    private String sign;

    @TableField("user_name")
    private String username;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Long createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

}
