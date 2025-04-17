package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;
import com.dji.sdk.cloudapi.control.MeteringModeEnum;

import java.util.Objects;

/**
 * 红外测温点
 *
 * @author Qfei
 * @date 2025/4/16 15:51
 */
public class IrMeteringPointSetImpl extends PayloadCommandsHandler {

    IrMeteringPointSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getX()) && Objects.nonNull(param.getY());
    }

    @Override
    public boolean canPublish(String deviceSn) {
        return super.canPublish(deviceSn)
                && MeteringModeEnum.SPOT == osdCamera.getIrMeteringMode();
    }
}
