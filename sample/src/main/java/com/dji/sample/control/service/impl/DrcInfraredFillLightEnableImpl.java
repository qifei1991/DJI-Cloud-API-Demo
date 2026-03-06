package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;

import java.util.Objects;

/**
 * 近红外补光使能
 *
 * @author Qfei
 * @date 2026/3/5 19:04
 */
public class DrcInfraredFillLightEnableImpl extends PayloadCommandsHandler {

    DrcInfraredFillLightEnableImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getEnable());
    }
}
