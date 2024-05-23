package com.dji.sdk.cloudapi.device;

import com.dji.sdk.common.BaseModel;

import java.util.List;

/**
 * @author Qfei
 * @date 2024/5/22 19:15
 */
public class PsdkUiResource extends BaseModel {
    private List<Object> psdkUiResource;

    @Override
    public String toString() {
        return "PsdkUiResource{" +
                "psdkUiResource=" + psdkUiResource +
                '}';
    }

    public List<Object> getPsdkUiResource() {
        return psdkUiResource;
    }

    public PsdkUiResource setPsdkUiResource(List<Object> psdkUiResource) {
        this.psdkUiResource = psdkUiResource;
        return this;
    }
}
