package com.dji.sample.manage.model.receiver;

import com.dji.sample.manage.annotation.PropertyParamPosition;
import com.dji.sample.manage.model.enums.PropertyParamEnum;
import com.dji.sdk.cloudapi.device.PayloadIndex;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * 测温区间下限
 *
 * @author Qfei
 * @date 2026/3/6 15:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@PropertyParamPosition(PropertyParamEnum.CHILD)
public class ThermalIsothermLowerLimitReceiver extends BasicDeviceProperty {

    @NotNull
    @Valid
    private PayloadIndex payloadIndex;

    @NotNull
    private Integer thermalIsothermLowerLimit;

    @Override
    public boolean valid() {
        return Objects.nonNull(thermalIsothermLowerLimit);
    }
}
