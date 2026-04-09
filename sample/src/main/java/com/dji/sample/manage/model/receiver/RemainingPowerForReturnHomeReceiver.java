package com.dji.sample.manage.model.receiver;

import com.dji.sdk.cloudapi.device.OsdDockDrone;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author Qfei
 * @date 2026/4/6 11:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemainingPowerForReturnHomeReceiver extends BasicDeviceProperty {

    private Integer remainingPowerForReturnHome;

    private static final int LIMIT_MAX = 100;

    private static final int LIMIT_MIN = 15;

    @Override
    public boolean valid() {
        return Objects.nonNull(this.remainingPowerForReturnHome) && this.remainingPowerForReturnHome >= LIMIT_MIN && this.remainingPowerForReturnHome <= LIMIT_MAX;
    }

    @Override
    public boolean canPublish(OsdDockDrone osd) {
        return remainingPowerForReturnHome.intValue() != osd.getBattery().getReturnHomePower();
    }
}
