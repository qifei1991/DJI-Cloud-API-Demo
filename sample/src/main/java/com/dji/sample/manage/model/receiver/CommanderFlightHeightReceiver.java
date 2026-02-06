package com.dji.sample.manage.model.receiver;

import com.dji.sdk.cloudapi.device.OsdDockDrone;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @author Qfei
 * @date 2026/1/27 9:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommanderFlightHeightReceiver extends BasicDeviceProperty {

    @NotNull
    @Min(2)
    @Max(3000)
    private Integer commanderFlightHeight;

    private static final int HEIGHT_LIMIT_MAX = 3000;

    private static final int HEIGHT_LIMIT_MIN = 2;

    @Override
    public String toString() {
        return "CommanderFlightHeightReceiver{" +
                "commanderFlightHeight=" + commanderFlightHeight +
                '}';
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(commanderFlightHeight)
                && commanderFlightHeight >= HEIGHT_LIMIT_MIN && commanderFlightHeight <= HEIGHT_LIMIT_MAX;
    }

    @Override
    public boolean canPublish(OsdDockDrone osd) {
        return super.canPublish(osd);
    }
}
