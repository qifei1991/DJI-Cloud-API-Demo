package com.dji.sample.interconnection.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dji.sample.interconnection.model.enums.SpeakerContentTypeEnum;
import com.dji.sdk.cloudapi.interconnection.PlayAudioFormatEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Qfei
 * @date 2024/4/24 14:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpeakerContentDTO {

    /**
     * uuid
     */
    @JsonProperty("id")
    private String interconnectionId;

    /**
     * interconnection name
     */
    private String name;

    /**
     * The play content type. 0: tts; 1: audio.
     */
    @TableField("type")
    private SpeakerContentTypeEnum type;

    /**
     * The key of the file in the bucket.
     */
    @TableField("object_key")
    private String objectKey;

    /**
     * 音频文件格式
     */
    @TableField("audio_format")
    private PlayAudioFormatEnum audioFormat;

    /**
     * The md5 of the interconnection file.
     */
    @TableField("sign")
    private String sign;

    /**
     * Which workspace the current play file belongs to.
     */
    private String workspaceId;

    /**
     * The name of the creator.
     */
    @TableField("user_name")
    private String username;

    /**
     *
     */
    private Long createTime;

    /**
     * required, can't modify.
     */
    private Long updateTime;

}
