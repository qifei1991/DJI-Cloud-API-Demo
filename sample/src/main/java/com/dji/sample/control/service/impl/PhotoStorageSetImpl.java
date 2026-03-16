package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;
import com.dji.sdk.cloudapi.device.CameraModeEnum;
import com.dji.sdk.cloudapi.device.CameraStateEnum;

import java.util.Objects;

/**
 * 照片存储设置
 *
 * @author Qfei
 * @date 2026/3/6 14:53
 */
public class PhotoStorageSetImpl extends PayloadCommandsHandler {

    public PhotoStorageSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getPayloadIndex()) && Objects.nonNull(param.getPhotoStorageSettings());
    }

    @Override
    public boolean canPublish(String deviceSn) {
        super.canPublish(deviceSn);

        return CameraStateEnum.WORKING != osdCamera.getPhotoState() && (
                CameraModeEnum.PHOTO == osdCamera.getCameraMode()
                        || CameraModeEnum.LOW_LIGHT_INTELLIGENCE == osdCamera.getCameraMode()
                        || CameraModeEnum.TIMING_PHOTO == osdCamera.getCameraMode());
    }
}
