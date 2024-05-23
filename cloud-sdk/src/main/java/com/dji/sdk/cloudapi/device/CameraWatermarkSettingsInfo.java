package com.dji.sdk.cloudapi.device;

import com.dji.sdk.common.BaseModel;

/**
 * @author Qfei
 * @date 2024/5/22 19:18
 */
public class CameraWatermarkSettingsInfo extends BaseModel {

    private CameraWatermarkSettings cameraWatermarkSettings;

    @Override
    public String toString() {
        return "CameraWatermarkSettingsInfo{" +
                "cameraWatermarkSettings=" + cameraWatermarkSettings +
                '}';
    }

    public CameraWatermarkSettings getCameraWatermarkSettings() {
        return cameraWatermarkSettings;
    }

    public CameraWatermarkSettingsInfo setCameraWatermarkSettings(CameraWatermarkSettings cameraWatermarkSettings) {
        this.cameraWatermarkSettings = cameraWatermarkSettings;
        return this;
    }
}
