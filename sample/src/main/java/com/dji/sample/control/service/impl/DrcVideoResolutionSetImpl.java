package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;

import java.util.Objects;

/**
 * 视频分辨率设置
 *
 * @author Qfei
 * @date 2026/3/6 14:31
 */
public class DrcVideoResolutionSetImpl extends PayloadCommandsHandler {

    DrcVideoResolutionSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getPayloadIndex()) && Objects.nonNull(param.getVideoResolution());
    }
}
