package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;
import com.dji.sdk.cloudapi.control.CameraTypeEnum;
import com.dji.sdk.cloudapi.control.ExposureModeEnum;
import com.dji.sdk.cloudapi.device.CameraStateEnum;

import java.util.Objects;

/**
 * 相机快门速度设置
 *
 * @author Qfei
 * @date 2026/3/5 16:43
 */
public class DrcCameraShutterSetImpl extends PayloadCommandsHandler {

    DrcCameraShutterSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getPayloadIndex())
                && Objects.nonNull(param.getCameraType()) && Objects.nonNull(param.getShutterValue())
                && (CameraTypeEnum.ZOOM == param.getCameraType() || CameraTypeEnum.WIDE == param.getCameraType());
    }

    @Override
    public boolean canPublish(String deviceSn) {
        super.canPublish(deviceSn);
        if (CameraStateEnum.WORKING == osdCamera.getPhotoState()) {
            return false;
        }
        switch (param.getCameraType()) {
            case WIDE:
                return ExposureModeEnum.MANUAL == osdCamera.getWideExposureMode()
                        && param.getShutterValue().getSpeed() != osdCamera.getWideShutterSpeed().getSpeed();
            case ZOOM:
                return ExposureModeEnum.MANUAL == osdCamera.getWideExposureMode()
                        && param.getShutterValue().getSpeed() != osdCamera.getZoomShutterSpeed().getSpeed();
        }
        return false;
    }
}
