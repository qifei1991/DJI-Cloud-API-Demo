package com.dji.sample.manage.model.receiver;

import com.dji.sample.manage.annotation.PropertyParamPosition;
import com.dji.sample.manage.model.enums.PropertyParamEnum;
import com.dji.sdk.cloudapi.device.DroneBattery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 飞机电池设置
 *
 * @author Qfei
 * @date 2026/4/6 9:52
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@PropertyParamPosition(PropertyParamEnum.CHILD)
public class DroneBatteryReceiver extends BasicDeviceProperty {

    private DroneBattery battery;

    @Override
    public boolean valid() {
        return Objects.nonNull(battery);
    }
}
