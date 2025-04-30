package com.dji.sample.manage.model.receiver;

import com.dji.sdk.cloudapi.device.OsdDockDrone;

/**
 * 空中回传
 *
 * @author Qfei
 * @date 2025/4/28 15:45
 */
public class AirTransferEnableReceiver extends BasicDeviceProperty {
    @Override
    public boolean valid() {
        return true;
    }

    @Override
    public boolean canPublish(OsdDockDrone osd) {
        return true;
    }
}
