package com.dji.sdk.cloudapi.interconnection;

import com.dji.sdk.common.BaseModel;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * set psdk widget
 *
 * @author Qfei
 * @date 2024/4/23 17:25
 */
public class PSDKWidgetValueSetRequest extends BaseModel {

    /**
     * psdk 负载设备索引
     */
    @NotNull
    @Range(min = 0, max = 3)
    private Integer psdkIndex;

    /**
     * 控件编号
     */
    @NotNull
    @Min(0)
    private Integer index;

    /**
     * 控件值(开关、进度等控件值由开发者自行定义)
     */
    @NotNull
    private Integer value;

    @Override
    public String toString() {
        return "PSDKWidgetValueSetRequest{" +
                "psdkIndex=" + psdkIndex +
                ", index=" + index +
                ", value=" + value +
                '}';
    }

    public Integer getPsdkIndex() {
        return psdkIndex;
    }

    public PSDKWidgetValueSetRequest setPsdkIndex(Integer psdkIndex) {
        this.psdkIndex = psdkIndex;
        return this;
    }

    public Integer getIndex() {
        return index;
    }

    public PSDKWidgetValueSetRequest setIndex(Integer index) {
        this.index = index;
        return this;
    }

    public Integer getValue() {
        return value;
    }

    public PSDKWidgetValueSetRequest setValue(Integer value) {
        this.value = value;
        return this;
    }
}
