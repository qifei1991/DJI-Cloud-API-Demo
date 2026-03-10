package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;
import com.dji.sdk.cloudapi.control.CameraTypeEnum;
import com.dji.sdk.cloudapi.control.ExposureModeEnum;
import com.dji.sdk.cloudapi.device.CameraStateEnum;

import java.util.Objects;

/**
 * 相机光圈值设置
 *
 * @author Qfei
 * @date 2026/3/5 16:31
 */
public class DrcCameraIsoSetImpl extends PayloadCommandsHandler {

    DrcCameraIsoSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getCameraType()) && Objects.nonNull(param.getIsoValue())
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
                        && param.getIsoValue().getIso() != osdCamera.getWideIso().getIso();
            case ZOOM:
                return ExposureModeEnum.MANUAL == osdCamera.getZoomExposureMode()
                        && param.getIsoValue().getIso() != osdCamera.getZoomIso().getIso();
        }
        return false;
    }
}
