package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;

import java.util.Objects;

/**
 * 夜景模式设置
 *
 * @author Qfei
 * @date 2026/3/5 18:49
 */
public class DrcCameraNightModeSetImpl extends PayloadCommandsHandler {

    DrcCameraNightModeSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getPayloadIndex());
    }
}
