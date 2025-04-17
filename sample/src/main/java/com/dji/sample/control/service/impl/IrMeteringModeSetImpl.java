package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;

import java.util.Objects;

/**
 * 红外测温模式设置
 *
 * @author Qfei
 * @date 2025/4/16 15:42
 */
public class IrMeteringModeSetImpl extends PayloadCommandsHandler {

    public IrMeteringModeSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getMode());
    }

    @Override
    public boolean canPublish(String deviceSn) {
        return super.canPublish(deviceSn)
                && param.getMode() != osdCamera.getIrMeteringMode();
    }
}
