package com.dji.sdk.cloudapi.interconnection;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 探照灯
 *
 * @author Qfei
 * @date 2025/11/27 17:00
 */
public class Searchlight {

    private Integer workMode;

    private Integer brightness;

    private CalibrationStatusEnum calibrationStatus;

    private Integer calibrationProgress;

    /**
     * 左灯微调值
     */
    @Min(-3)
    @Max(3)
    private Integer leftValue;

    /**
     * 右灯微调值
     */
    @Min(-3)
    @Max(3)
    private Integer rightValue;

    /**
     * 探照灯广视野模式是否开启
     */
    private Boolean wideFieldMode;

    /**
     * 探照灯云台联动是否开启
     */
    private Boolean lightGimbalControl;

    public Searchlight() {
    }

    @Override
    public String toString() {
        return "Searchlight{" +
                "workMode=" + workMode +
                ", brightness=" + brightness +
                ", calibrationStatus=" + calibrationStatus +
                ", calibrationProgress=" + calibrationProgress +
                ", leftValue=" + leftValue +
                ", rightValue=" + rightValue +
                ", wideFieldMode=" + wideFieldMode +
                ", lightGimbalControl=" + lightGimbalControl +
                '}';
    }

    public Integer getWorkMode() {
        return workMode;
    }

    public Integer getBrightness() {
        return brightness;
    }

    public Searchlight setBrightness(Integer brightness) {
        this.brightness = brightness;
        return this;
    }

    public CalibrationStatusEnum getCalibrationStatus() {
        return calibrationStatus;
    }

    public Searchlight setCalibrationStatus(CalibrationStatusEnum calibrationStatus) {
        this.calibrationStatus = calibrationStatus;
        return this;
    }

    public Integer getCalibrationProgress() {
        return calibrationProgress;
    }

    public Searchlight setCalibrationProgress(Integer calibrationProgress) {
        this.calibrationProgress = calibrationProgress;
        return this;
    }

    public Integer getLeftValue() {
        return leftValue;
    }

    public Searchlight setLeftValue(Integer leftValue) {
        this.leftValue = leftValue;
        return this;
    }

    public Integer getRightValue() {
        return rightValue;
    }

    public Searchlight setRightValue(Integer rightValue) {
        this.rightValue = rightValue;
        return this;
    }

    public Boolean getWideFieldMode() {
        return wideFieldMode;
    }

    public Searchlight setWideFieldMode(Boolean wideFieldMode) {
        this.wideFieldMode = wideFieldMode;
        return this;
    }

    public Boolean getLightGimbalControl() {
        return lightGimbalControl;
    }

    public Searchlight setLightGimbalControl(Boolean lightGimbalControl) {
        this.lightGimbalControl = lightGimbalControl;
        return this;
    }

    public Searchlight setWorkMode(Integer workMode) {
        this.workMode = workMode;
        return this;
    }
}
