package com.dji.sample.manage.model.receiver;

import com.dji.sample.manage.annotation.PropertyParamPosition;
import com.dji.sample.manage.model.enums.PropertyParamEnum;
import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.cloudapi.device.ThermalGainModeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * 热增益模式设置
 *
 * @author Qfei
 * @date 2026/3/6 15:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@PropertyParamPosition(PropertyParamEnum.CHILD)
public class ThermalGainModeReceiver extends BasicDeviceProperty {

    @Valid
    @NotNull
    private PayloadIndex payloadIndex;

    @NotNull
    private ThermalGainModeEnum thermalGainMode;

    @Override
    public boolean valid() {
        return Objects.nonNull(thermalGainMode);
    }
}
