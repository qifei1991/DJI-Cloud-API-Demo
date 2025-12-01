package com.dji.sample.psdk.model.dto;

import com.dji.sample.psdk.model.enums.SpeakerContentTypeEnum;
import com.dji.sdk.cloudapi.psdk.PlayAudioFormatEnum;
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
    private String contentId;

    /**
     * interconnection name
     */
    private String name;

    /**
     * The play content type. 0: tts; 1: audio.
     */
    private SpeakerContentTypeEnum type;

    /**
     * The key of the file in the bucket.
     */
    private String objectKey;

    /**
     * 音频文件格式
     */
    private PlayAudioFormatEnum audioFormat;

    /**
     * The md5 of the interconnection file.
     */
    private String sign;

    /**
     * Which workspace the current play file belongs to.
     */
    private String workspaceId;

    /**
     * The name of the creator.
     */
    @JsonProperty("user_name")
    private String username;

    /**
     *
     */
    private Long createTime;

    /**
     * required, can't modify.
     */
    private Long updateTime;

    private String organizationCode;
}
