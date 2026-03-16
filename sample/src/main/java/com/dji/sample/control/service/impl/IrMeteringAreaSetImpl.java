package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;
import com.dji.sdk.cloudapi.control.MeteringModeEnum;

import java.util.Objects;

/**
 * 红外测温区设置
 * @author Qfei
 * @date 2025/4/16 15:53
 */
public class IrMeteringAreaSetImpl extends PayloadCommandsHandler {

    IrMeteringAreaSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getPayloadIndex()) && Objects.nonNull(param.getX()) && Objects.nonNull(param.getY())
                && Objects.nonNull(param.getWidth()) && Objects.nonNull(param.getHeight());
    }

    @Override
    public boolean canPublish(String deviceSn) {
        return super.canPublish(deviceSn)
                && MeteringModeEnum.AREA == osdCamera.getIrMeteringMode();
    }
}
