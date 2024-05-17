package com.dji.sdk.cloudapi.device;

import com.dji.sdk.common.BaseModel;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 实名制
 *
 * @author Qfei
 * @date 2024/5/14 19:26
 */
public class UomRealNameState extends BaseModel {

    @JsonProperty("uom_real_name_state")
    private Integer uomRealNameState;

    @Override
    public String toString() {
        return "UomRealNameState{" +
                "uomRealNameState=" + uomRealNameState +
                '}';
    }

    public Integer getUomRealNameState() {
        return uomRealNameState;
    }

    public UomRealNameState setUomRealNameState(Integer uomRealNameState) {
        this.uomRealNameState = uomRealNameState;
        return this;
    }
}
