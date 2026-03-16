package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;
import com.dji.sdk.cloudapi.control.CameraTypeEnum;
import com.dji.sdk.cloudapi.control.ExposureModeEnum;
import com.dji.sdk.cloudapi.device.CameraStateEnum;

import java.util.Objects;

/**
 * 镜头曝光值设置
 * @author Qfei
 * @date 2026/3/5 14:58
 */
public class CameraExposureSetValueImpl extends PayloadCommandsHandler {

    CameraExposureSetValueImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getPayloadIndex())
                && Objects.nonNull(param.getCameraType()) && Objects.nonNull(param.getExposureValue())
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
                return ExposureModeEnum.AUTO == osdCamera.getWideExposureMode()
                        && param.getExposureValue() != osdCamera.getWideExposureValue();
            case ZOOM:
                return ExposureModeEnum.AUTO == osdCamera.getZoomExposureMode()
                        && param.getExposureValue() != osdCamera.getZoomExposureValue();
        }
        return false;
    }
}
