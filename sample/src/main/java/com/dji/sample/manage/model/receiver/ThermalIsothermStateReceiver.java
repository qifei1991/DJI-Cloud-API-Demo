package com.dji.sample.manage.model.receiver;

import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.cloudapi.device.SwitchActionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * 开启等温线
 *
 * @author Qfei
 * @date 2026/3/6 15:33
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ThermalIsothermStateReceiver extends BasicDeviceProperty {

    @NotNull
    @Valid
    private PayloadIndex payloadIndex;

    @NotNull
    private SwitchActionEnum thermalIsothermState;

    @Override
    public boolean valid() {
        return Objects.nonNull(thermalIsothermState);
    }
}
