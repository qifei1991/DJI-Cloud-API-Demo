package com.dji.sdk.cloudapi.control;

import com.dji.sdk.cloudapi.device.SwitchActionEnum;
import com.dji.sdk.common.BaseModel;

import javax.validation.constraints.NotNull;

/**
 * 隐蔽模式
 *
 * @author Qfei
 * @date 2026/3/6 10:51
 */
public class DrcStealthStateSetRequest extends BaseModel {

    @NotNull
    private SwitchActionEnum stealthState;

    public DrcStealthStateSetRequest() {
    }

    @Override
    public String toString() {
        return "DrcStealthStateSetRequest{" +
                "stealthState=" + stealthState +
                '}';
    }

    public SwitchActionEnum getStealthState() {
        return stealthState;
    }

    public DrcStealthStateSetRequest setStealthState(SwitchActionEnum stealthState) {
        this.stealthState = stealthState;
        return this;
    }
}
