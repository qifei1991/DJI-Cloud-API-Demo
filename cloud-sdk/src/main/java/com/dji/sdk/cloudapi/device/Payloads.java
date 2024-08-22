package com.dji.sdk.cloudapi.device;

/**
 * @author Qfei
 * @date 2024/8/13 19:09
 */
public class Payloads {

    private ControlSourceEnum controlSource;

    private PayloadIndex payloadIndex;

    @Override
    public String toString() {
        return "Payloads{" +
                "controlSource=" + controlSource +
                ", payloadIndex=" + payloadIndex +
                '}';
    }

    public ControlSourceEnum getControlSource() {
        return controlSource;
    }

    public Payloads setControlSource(ControlSourceEnum controlSource) {
        this.controlSource = controlSource;
        return this;
    }

    public PayloadIndex getPayloadIndex() {
        return payloadIndex;
    }

    public Payloads setPayloadIndex(PayloadIndex payloadIndex) {
        this.payloadIndex = payloadIndex;
        return this;
    }
}
