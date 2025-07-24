package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;
import com.dji.sdk.cloudapi.control.CameraTypeEnum;

import java.util.Objects;

/**
 * 点对焦参数校验
 *
 * @author Qfei
 * @date 2025/7/24 10:42
 */
public class CameraPointFocusActionImpl extends PayloadCommandsHandler {

    CameraPointFocusActionImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getCameraType())
                && Objects.nonNull(param.getX()) && Objects.nonNull(param.getY())
                && CameraTypeEnum.ZOOM == param.getCameraType();
    }

}
