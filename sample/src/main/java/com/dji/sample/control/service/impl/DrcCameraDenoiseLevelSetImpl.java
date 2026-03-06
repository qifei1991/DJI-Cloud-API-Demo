package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;

import java.util.Objects;

/**
 * 夜景降噪等级设置
 *
 * @author Qfei
 * @date 2026/3/5 18:55
 */
public class DrcCameraDenoiseLevelSetImpl extends PayloadCommandsHandler {

    DrcCameraDenoiseLevelSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getLevel());
    }

}
