package com.dji.sdk.cloudapi.control;

import com.dji.sdk.cloudapi.device.SwitchActionEnum;
import com.dji.sdk.common.BaseModel;

import javax.validation.constraints.NotNull;

/**
 * 夜航灯设置
 *
 * @author Qfei
 * @date 2026/3/6 13:48
 */
public class DrcNightLightsStateSetRequest extends BaseModel {

    @NotNull
    private SwitchActionEnum nightLightsState;

    public DrcNightLightsStateSetRequest() {
    }

    @Override
    public String toString() {
        return "DrcNightLightsStateSetRequest{" +
                "nightLightsState=" + nightLightsState +
                '}';
    }

    public SwitchActionEnum getNightLightsState() {
        return nightLightsState;
    }

    public DrcNightLightsStateSetRequest setNightLightsState(SwitchActionEnum nightLightsState) {
        this.nightLightsState = nightLightsState;
        return this;
    }
}
