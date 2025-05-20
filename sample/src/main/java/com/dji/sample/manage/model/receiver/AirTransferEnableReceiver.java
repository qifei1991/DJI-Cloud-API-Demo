package com.dji.sample.manage.model.receiver;

import com.dji.sdk.cloudapi.device.OsdDockDrone;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 空中回传
 *
 * @author Qfei
 * @date 2025/4/28 15:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AirTransferEnableReceiver extends BasicDeviceProperty {

    private Boolean airTransferEnable;

    @Override
    public boolean valid() {
        return true;
    }

    @Override
    public boolean canPublish(OsdDockDrone osd) {
        return true;
    }
}
