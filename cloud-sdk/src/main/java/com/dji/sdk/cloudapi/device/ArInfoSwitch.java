package com.dji.sdk.cloudapi.device;

/**
 * @author Qfei
 * @date 2024/8/13 19:03
 */
public class ArInfoSwitch {

    private Integer arInfoSwitch;

    @Override
    public String toString() {
        return "ArInfoSwitch{" +
                "arInfoSwitch=" + arInfoSwitch +
                '}';
    }

    public Integer getArInfoSwitch() {
        return arInfoSwitch;
    }

    public ArInfoSwitch setArInfoSwitch(Integer arInfoSwitch) {
        this.arInfoSwitch = arInfoSwitch;
        return this;
    }
}
