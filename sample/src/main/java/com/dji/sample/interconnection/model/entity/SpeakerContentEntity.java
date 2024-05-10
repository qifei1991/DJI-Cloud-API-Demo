package com.dji.sample.interconnection.model.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
* 互联互通文件
* @TableName interconnection_file
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("speaker_content")
public class SpeakerContentEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
    * uuid
    */
    @TableField("interconnection_id")
    private String interconnectionId;

    /**
    * interconnection name
    */
    @TableField("name")
    private String name;

    /**
     * The play content type. 0: tts; 1: audio.
     */
    @TableField("type")
    private Integer type;

    /**
     * The key of the file in the bucket.
     */
    @TableField("object_key")
    private String objectKey;

    /**
     * 音频文件格式
     */
    @TableField("audio_format")
    private String audioFormat;

    /**
    * The md5 of the interconnection file.
    */
    @TableField("sign")
    private String sign;

    /**
    * Which workspace the current play file belongs to.
    */
    @TableField("workspace_id")
    private String workspaceId;

    /**
    * The name of the creator.
    */
    @TableField("user_name")
    private String username;

    /**
    *
    */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Long createTime;

    /**
    * required, can't modify.
    */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

}
