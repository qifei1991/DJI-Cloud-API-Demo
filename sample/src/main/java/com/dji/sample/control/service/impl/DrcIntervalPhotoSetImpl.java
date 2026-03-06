package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;

import java.util.Objects;

/**
 * 拍照间隔
 *
 * @author Qfei
 * @date 2026/3/6 17:19
 */
public class DrcIntervalPhotoSetImpl extends PayloadCommandsHandler {
    DrcIntervalPhotoSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getInterval());
    }
}
