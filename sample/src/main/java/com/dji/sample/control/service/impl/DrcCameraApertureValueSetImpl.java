package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;
import com.dji.sdk.cloudapi.control.CameraTypeEnum;
import com.dji.sdk.cloudapi.device.CameraStateEnum;

import java.util.Objects;

/**
 * 相机快门设置
 *
 * @author Qfei
 * @date 2026/3/5 16:41
 */
public class DrcCameraApertureValueSetImpl extends PayloadCommandsHandler {

    DrcCameraApertureValueSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getCameraType()) && Objects.nonNull(param.getApertureValue())
                && (CameraTypeEnum.ZOOM == param.getCameraType() || CameraTypeEnum.WIDE == param.getCameraType());
    }

    @Override
    public boolean canPublish(String deviceSn) {
        super.canPublish(deviceSn);
        return CameraStateEnum.WORKING != osdCamera.getPhotoState();
    }
}
