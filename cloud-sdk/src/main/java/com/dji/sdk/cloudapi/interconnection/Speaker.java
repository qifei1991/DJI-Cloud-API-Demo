package com.dji.sdk.cloudapi.interconnection;

/**
 * @author Qfei
 * @date 2025/8/8 16:51
 */
public class Speaker {

    private SpeakerWorkMode workMode;

    private PlayModeEnum playMode;

    private Integer playVolume;

    private SpeakerSystemStatus systemStatus;

    private String playFileName;

    private String playFileMd5;

    public Speaker() {
    }

    @Override
    public String toString() {
        return "Speaker{" +
                "workMode=" + workMode +
                ", playMode=" + playMode +
                ", playVolume=" + playVolume +
                ", systemStatus=" + systemStatus +
                ", playFileName='" + playFileName + '\'' +
                ", playFileMd5='" + playFileMd5 + '\'' +
                '}';
    }

    public SpeakerWorkMode getWorkMode() {
        return workMode;
    }

    public Speaker setWorkMode(SpeakerWorkMode workMode) {
        this.workMode = workMode;
        return this;
    }

    public PlayModeEnum getPlayMode() {
        return playMode;
    }

    public Speaker setPlayMode(PlayModeEnum playMode) {
        this.playMode = playMode;
        return this;
    }

    public Integer getPlayVolume() {
        return playVolume;
    }

    public Speaker setPlayVolume(Integer playVolume) {
        this.playVolume = playVolume;
        return this;
    }

    public SpeakerSystemStatus getSystemStatus() {
        return systemStatus;
    }

    public Speaker setSystemStatus(SpeakerSystemStatus systemStatus) {
        this.systemStatus = systemStatus;
        return this;
    }

    public String getPlayFileName() {
        return playFileName;
    }

    public Speaker setPlayFileName(String playFileName) {
        this.playFileName = playFileName;
        return this;
    }

    public String getPlayFileMd5() {
        return playFileMd5;
    }

    public Speaker setPlayFileMd5(String playFileMd5) {
        this.playFileMd5 = playFileMd5;
        return this;
    }
}
