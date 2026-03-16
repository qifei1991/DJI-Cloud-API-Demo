package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;
import com.dji.sdk.cloudapi.control.CameraTypeEnum;
import com.dji.sdk.cloudapi.device.CameraStateEnum;

import java.util.Objects;

/**
 * 对焦模式设置实现
 *
 * @author Qfei
 * @date 2025/7/24 10:07
 */
public class CameraFocusModeSetImpl extends PayloadCommandsHandler {

    public CameraFocusModeSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getPayloadIndex())
                && Objects.nonNull(param.getCameraType()) && Objects.nonNull(param.getFocusMode())
                && (CameraTypeEnum.ZOOM == param.getCameraType() || CameraTypeEnum.WIDE == param.getCameraType());
    }

    @Override
    public boolean canPublish(String deviceSn) {
        super.canPublish(deviceSn);
        if (CameraStateEnum.WORKING == osdCamera.getPhotoState()/*  || FocusStateEnum.IDLE != osdCamera.getZoomFocusState() */) {
            return false;
        }
        return param.getFocusMode() != osdCamera.getZoomFocusMode();
    }
}
