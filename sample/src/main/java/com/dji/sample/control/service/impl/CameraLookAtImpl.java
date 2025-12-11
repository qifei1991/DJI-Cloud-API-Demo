package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;

import java.util.Objects;

/**
 * @author Qfei
 * @date 2025/12/11 16:08
 */
public class CameraLookAtImpl extends PayloadCommandsHandler {

    CameraLookAtImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getLatitude()) && Objects.nonNull(param.getLongitude()) && Objects.nonNull(param.getHeight());
    }

}
