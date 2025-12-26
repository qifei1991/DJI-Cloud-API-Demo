package com.dji.sdk.cloudapi.psdk;

import com.dji.sdk.cloudapi.device.TtsLanguageEnum;
import com.dji.sdk.cloudapi.device.TtsPlayTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author Qfei
 * @date 2025/8/8 16:51
 */
public class Speaker {

    private SpeakerWorkMode workMode;

    private PlayModeEnum playMode;

    @Min(0)
    @Max(100)
    private Integer playVolume;

    private SpeakerSystemStatus systemState;

    private String playFileName;

    @JsonProperty("play_file_md5")
    private String playFileMd5;

    @Min(0)
    @Max(100)
    private Integer ttsVolume;

    private TtsPlayTypeEnum ttsType;

    private TtsLanguageEnum ttsLanguage;

    @Min(1)
    @Max(100)
    private Integer ttsSpeed;

    public Speaker() {
    }

    @Override
    public String toString() {
        return "Speaker{" +
                "workMode=" + workMode +
                ", playMode=" + playMode +
                ", playVolume=" + playVolume +
                ", systemState=" + systemState +
                ", playFileName='" + playFileName + '\'' +
                ", playFileMd5='" + playFileMd5 + '\'' +
                ", ttsVolume=" + ttsVolume +
                ", ttsType=" + ttsType +
                ", ttsLanguage=" + ttsLanguage +
                ", ttsSpeed=" + ttsSpeed +
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

    public SpeakerSystemStatus getSystemState() {
        return systemState;
    }

    public Speaker setSystemState(SpeakerSystemStatus systemState) {
        this.systemState = systemState;
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

    public Integer getTtsVolume() {
        return ttsVolume;
    }

    public Speaker setTtsVolume(Integer ttsVolume) {
        this.ttsVolume = ttsVolume;
        return this;
    }

    public TtsPlayTypeEnum getTtsType() {
        return ttsType;
    }

    public Speaker setTtsType(TtsPlayTypeEnum ttsType) {
        this.ttsType = ttsType;
        return this;
    }

    public TtsLanguageEnum getTtsLanguage() {
        return ttsLanguage;
    }

    public Speaker setTtsLanguage(TtsLanguageEnum ttsLanguage) {
        this.ttsLanguage = ttsLanguage;
        return this;
    }

    public Integer getTtsSpeed() {
        return ttsSpeed;
    }

    public Speaker setTtsSpeed(Integer ttsSpeed) {
        this.ttsSpeed = ttsSpeed;
        return this;
    }
}
