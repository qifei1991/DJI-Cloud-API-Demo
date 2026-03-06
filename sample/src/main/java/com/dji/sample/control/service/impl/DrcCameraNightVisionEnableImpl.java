package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;

import java.util.Objects;

/**
 * 黑白夜视使能
 *
 * @author Qfei
 * @date 2026/3/5 19:03
 */
public class DrcCameraNightVisionEnableImpl extends PayloadCommandsHandler {

    DrcCameraNightVisionEnableImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getEnable());
    }
}
