package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;
import com.dji.sdk.cloudapi.control.CameraTypeEnum;
import com.dji.sdk.cloudapi.device.CameraStateEnum;

import java.util.Objects;

/**
 * 镜头曝光模式设置
 *
 * @author Qfei
 * @date 2026/3/5 15:12
 */
public class CameraExposureModeSetImpl extends PayloadCommandsHandler {

    CameraExposureModeSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getCameraType()) && Objects.nonNull(param.getExposureMode())
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
                return param.getExposureMode() != osdCamera.getWideExposureMode();
            case ZOOM:
                return param.getExposureMode() != osdCamera.getZoomExposureMode();
        }
        return false;
    }
}
