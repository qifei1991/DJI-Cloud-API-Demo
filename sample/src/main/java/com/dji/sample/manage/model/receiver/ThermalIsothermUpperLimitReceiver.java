package com.dji.sample.manage.model.receiver;

import com.dji.sdk.cloudapi.device.PayloadIndex;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * 测温区间上限
 *
 * @author Qfei
 * @date 2026/3/6 15:40
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ThermalIsothermUpperLimitReceiver extends BasicDeviceProperty {

    @NotNull
    @Valid
    private PayloadIndex payloadIndex;

    @NotNull
    private Integer thermalIsothermUpperLimit;

    @Override
    public boolean valid() {
        return Objects.nonNull(thermalIsothermUpperLimit);
    }
}
