package com.dji.sdk.cloudapi.property;

import com.dji.sdk.cloudapi.device.DroneBattery;
import com.dji.sdk.common.BaseModel;

/**
 * 电池设置
 *
 * @author Qfei
 * @date 2026/4/6 9:32
 */
public class DroneBatterySet extends BaseModel {

    private DroneBattery battery;

    public DroneBatterySet() {
    }

    @Override
    public String toString() {
        return "DroneBatterySet{" +
                "battery=" + battery +
                '}';
    }

    public DroneBatterySet setBattery(DroneBattery battery) {
        this.battery = battery;
        return this;
    }

    public DroneBattery getBattery() {
        return battery;
    }
}
