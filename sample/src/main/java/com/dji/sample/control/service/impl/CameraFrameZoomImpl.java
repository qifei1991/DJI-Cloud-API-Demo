package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;

import java.util.Objects;

/**
 * 框选变焦
 *
 * @author Qfei
 * @date 2026/3/13 17:46
 */
public class CameraFrameZoomImpl extends PayloadCommandsHandler {

    CameraFrameZoomImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getCameraType())
                && Objects.nonNull(param.getLocked())
                && Objects.nonNull(param.getX()) && Objects.nonNull(param.getY())
                && Objects.nonNull(param.getWidth()) && Objects.nonNull(param.getHeight());
    }

}
