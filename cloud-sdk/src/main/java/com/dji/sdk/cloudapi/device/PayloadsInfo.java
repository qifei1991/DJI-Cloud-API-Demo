package com.dji.sdk.cloudapi.device;

import com.dji.sdk.common.BaseModel;

import java.util.List;

/**
 * @author Qfei
 * @date 2024/8/13 19:10
 */
public class PayloadsInfo extends BaseModel {

    private List<Payloads> payloads;

    @Override
    public String toString() {
        return "PayloadsInfo{" +
                "payloads=" + payloads +
                '}';
    }

    public List<Payloads> getPayloads() {
        return payloads;
    }

    public PayloadsInfo setPayloads(List<Payloads> payloads) {
        this.payloads = payloads;
        return this;
    }
}
