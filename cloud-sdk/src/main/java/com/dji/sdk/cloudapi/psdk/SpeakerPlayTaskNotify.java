package com.dji.sdk.cloudapi.psdk;

/**
 * 喊话器播放进度通知
 *
 * @author Qfei
 * @date 2024/8/12 16:40
 */
public class SpeakerPlayTaskNotify {

    private String jobId;

    private String name;

    private Integer psdkIndex;

    private SpeakerJobStatusEnum status;

    // @Range(min = 0, max = 100)
    private Integer volume;

    private PlayModeEnum mode;

    @Override
    public String toString() {
        return "SpeakerPlayTaskNotify{" +
                "jobId='" + jobId + '\'' +
                ", name='" + name + '\'' +
                ", psdkIndex=" + psdkIndex +
                ", status=" + status +
                ", volume=" + volume +
                ", mode=" + mode +
                '}';
    }

    public String getJobId() {
        return jobId;
    }

    public SpeakerPlayTaskNotify setJobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    public String getName() {
        return name;
    }

    public SpeakerPlayTaskNotify setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getPsdkIndex() {
        return psdkIndex;
    }

    public SpeakerPlayTaskNotify setPsdkIndex(Integer psdkIndex) {
        this.psdkIndex = psdkIndex;
        return this;
    }

    public SpeakerJobStatusEnum getStatus() {
        return status;
    }

    public SpeakerPlayTaskNotify setStatus(SpeakerJobStatusEnum status) {
        this.status = status;
        return this;
    }

    public Integer getVolume() {
        return volume;
    }

    public SpeakerPlayTaskNotify setVolume(Integer volume) {
        this.volume = volume;
        return this;
    }

    public PlayModeEnum getMode() {
        return mode;
    }

    public SpeakerPlayTaskNotify setMode(PlayModeEnum mode) {
        this.mode = mode;
        return this;
    }
}
