package com.dji.sdk.cloudapi.device;

import com.dji.sdk.common.BaseModel;

import java.util.List;

/**
 * @author Qfei
 * @date 2024/5/22 19:17
 */
public class PsdkWidgetValues extends BaseModel {

    private List<PsdkWidget> psdkWidgetValues;

    @Override
    public String toString() {
        return "PsdkWidgetValues{" +
                "psdkWidgetValues=" + psdkWidgetValues +
                '}';
    }

    public List<PsdkWidget> getPsdkWidgetValues() {
        return psdkWidgetValues;
    }

    public PsdkWidgetValues setPsdkWidgetValues(List<PsdkWidget> psdkWidgetValues) {
        this.psdkWidgetValues = psdkWidgetValues;
        return this;
    }
}
