package com.dji.sample.interconnection.model.dto;

import com.dji.sdk.cloudapi.interconnection.SpeakerJobStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 喊话器任务
 *
 * @author Qfei
 * @date 2024/8/6 11:19
 */
@Data
@Builder
public class SpeakerJobDTO {

    /**
     * uuid
     */
    @JsonProperty("id")
    private String jobId;

    /**
     * job name
     */
    private String name;

    private String contentId;

    /**
     * The play mode. 1: success; 1: failure.
     */
    private SpeakerJobStatusEnum status;

    private String deviceSn;

    private String workspaceId;

    private String username;

    private Long createTime;

    private Long updateTime;

}
