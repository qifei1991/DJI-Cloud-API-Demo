package com.dji.sample.interconnection.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 喊话器job实体类
 *
 * @author Qfei
 * @date 2024/8/5 15:53
 */
@AllArgsConstructor
@NoArgsConstructor
@TableName("speaker_job")
public class SpeakerJobEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * uuid
     */
    @TableField("job_id")
    private String jobId;

    /**
     * interconnection name
     */
    @TableField("name")
    private String name;

    @TableField("content_id")
    private String contentId;

    /**
     * The play mode. 1: success; 1: failure.
     */
    @TableField("status")
    private Integer status;

    /**
     * The Device SN.
     */
    @TableField("device_sn")
    private String deviceSn;

    /**
     * Which workspace the current play job belongs to.
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

    @Override
    public String toString() {
        return "SpeakerJobEntity{" +
                "id=" + id +
                ", jobId='" + jobId + '\'' +
                ", name='" + name + '\'' +
                ", contentId='" + contentId + '\'' +
                ", status=" + status +
                ", deviceSn='" + deviceSn + '\'' +
                ", workspaceId='" + workspaceId + '\'' +
                ", username='" + username + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public SpeakerJobEntity setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getJobId() {
        return jobId;
    }

    public SpeakerJobEntity setJobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    public String getName() {
        return name;
    }

    public SpeakerJobEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getContentId() {
        return contentId;
    }

    public SpeakerJobEntity setContentId(String contentId) {
        this.contentId = contentId;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public SpeakerJobEntity setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public SpeakerJobEntity setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
        return this;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public SpeakerJobEntity setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public SpeakerJobEntity setUsername(String username) {
        this.username = username;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public SpeakerJobEntity setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public SpeakerJobEntity setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }
}
