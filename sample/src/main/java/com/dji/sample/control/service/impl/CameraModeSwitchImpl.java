package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;
import com.dji.sdk.cloudapi.device.CameraStateEnum;

import java.util.Objects;

/**
 * @author sean
 * @version 1.4
 * @date 2023/4/23
 */
public class CameraModeSwitchImpl extends PayloadCommandsHandler {

    public CameraModeSwitchImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getCameraMode());
    }

    @Override
    public boolean canPublish(String deviceSn) {
        super.canPublish(deviceSn);
        // todo 如果 param.getCameraMode() = 3, 需要判断剩余照片数量是否能支持全景拍照的数量
        return param.getCameraMode() != osdCamera.getCameraMode()
                && CameraStateEnum.IDLE == osdCamera.getPhotoState()
                && CameraStateEnum.IDLE == osdCamera.getRecordingState();
    }
}
