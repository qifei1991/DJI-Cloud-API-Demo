package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;

import java.util.Objects;

/**
 * 隐蔽模式设置
 *
 * @author Qfei
 * @date 2026/3/6 10:55
 */
public class DrcStealthStateSetImpl extends PayloadCommandsHandler {

    DrcStealthStateSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getStealthState());
    }
}
