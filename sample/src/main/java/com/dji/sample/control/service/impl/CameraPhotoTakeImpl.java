package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;
import com.dji.sdk.cloudapi.device.CameraModeEnum;
import com.dji.sdk.cloudapi.device.CameraStateEnum;

import java.util.Objects;

/**
 * @author sean
 * @version 1.4
 * @date 2023/4/23
 */
public class CameraPhotoTakeImpl extends PayloadCommandsHandler {

    public CameraPhotoTakeImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getPayloadIndex());
    }

    @Override
    public boolean canPublish(String deviceSn) {
        super.canPublish(deviceSn);

        // 如果 param.getCameraMode() = 3, 需要判断剩余照片数量是否能支持全景拍照的数量
        if (osdCamera.getCameraMode() == CameraModeEnum.PANORAMA && osdCamera.getRemainPhotoNum() < 25) {
            throw new RuntimeException(String.format("飞机剩余拍照数量[%s]不满足拍摄一组全景图片", osdCamera.getRemainPhotoNum()));
        }

        return CameraStateEnum.WORKING != osdCamera.getPhotoState() && osdCamera.getRemainPhotoNum() > 0;
    }
}
