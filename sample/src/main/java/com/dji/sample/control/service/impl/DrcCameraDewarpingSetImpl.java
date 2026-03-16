package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;
import com.dji.sdk.cloudapi.control.CameraTypeEnum;

import java.util.Objects;

/**
 * 镜头去畸变
 *
 * @author Qfei
 * @date 2026/3/6 9:35
 */
public class DrcCameraDewarpingSetImpl extends PayloadCommandsHandler {

    DrcCameraDewarpingSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getPayloadIndex())
                && Objects.nonNull(param.getCameraType()) && Objects.nonNull(param.getDewarpingState())
                && CameraTypeEnum.WIDE == param.getCameraType();
    }

}
