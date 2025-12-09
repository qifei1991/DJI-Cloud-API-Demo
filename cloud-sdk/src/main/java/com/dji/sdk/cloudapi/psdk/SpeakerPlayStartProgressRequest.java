package com.dji.sdk.cloudapi.psdk;

/**
 * 喊话器播放进度
 *
 * @author Qfei
 * @date 2025/12/9 18:19
 */
public class SpeakerPlayStartProgressRequest {

    private Integer psdkIndex;

    private SpeakerPlayStartStatus status;

    private String md5;

    private SpeakerPlayStartProgress progress;

    @Override
    public String toString() {
        return "SpeakerPlayStartProgressRequest{" +
                "psdkIndex=" + psdkIndex +
                ", status=" + status +
                ", md5='" + md5 + '\'' +
                ", progress=" + progress +
                '}';
    }

    public Integer getPsdkIndex() {
        return psdkIndex;
    }

    public SpeakerPlayStartProgressRequest setPsdkIndex(Integer psdkIndex) {
        this.psdkIndex = psdkIndex;
        return this;
    }

    public SpeakerPlayStartStatus getStatus() {
        return status;
    }

    public SpeakerPlayStartProgressRequest setStatus(SpeakerPlayStartStatus status) {
        this.status = status;
        return this;
    }

    public String getMd5() {
        return md5;
    }

    public SpeakerPlayStartProgressRequest setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public SpeakerPlayStartProgress getProgress() {
        return progress;
    }

    public SpeakerPlayStartProgressRequest setProgress(SpeakerPlayStartProgress progress) {
        this.progress = progress;
        return this;
    }
}
