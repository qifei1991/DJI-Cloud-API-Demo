package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;
import com.dji.sdk.cloudapi.control.CameraTypeEnum;
import com.dji.sdk.cloudapi.device.CameraStateEnum;
import com.dji.sdk.cloudapi.device.FocusStateEnum;

import java.util.Objects;

/**
 * 相机对焦值设置
 * @author Qfei
 * @date 2026/3/5 14:44
 */
public class CameraFocusValueSetImpl extends PayloadCommandsHandler {

    CameraFocusValueSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getPayloadIndex())
                && Objects.nonNull(param.getCameraType()) && Objects.nonNull(param.getFocusValue())
                && (CameraTypeEnum.ZOOM == param.getCameraType() || CameraTypeEnum.WIDE == param.getCameraType());
    }

    @Override
    public boolean canPublish(String deviceSn) {
        super.canPublish(deviceSn);
        if (CameraStateEnum.WORKING == osdCamera.getPhotoState() || FocusStateEnum.IDLE != osdCamera.getZoomFocusState()) {
            return false;
        }
        return Objects.equals(param.getFocusValue(), osdCamera.getZoomFocusValue());
    }
}
