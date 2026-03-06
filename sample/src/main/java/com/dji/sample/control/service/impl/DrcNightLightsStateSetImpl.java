package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;

import java.util.Objects;

/**
 * 夜航灯设置
 *
 * @author Qfei
 * @date 2026/3/6 13:52
 */
public class DrcNightLightsStateSetImpl extends PayloadCommandsHandler {

    DrcNightLightsStateSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getNightLightsState());
    }
}
