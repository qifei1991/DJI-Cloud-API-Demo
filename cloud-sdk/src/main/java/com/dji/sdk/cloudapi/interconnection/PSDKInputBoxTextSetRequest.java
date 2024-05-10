package com.dji.sdk.cloudapi.interconnection;

import com.dji.sdk.common.BaseModel;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Qfei
 * @date 2024/4/23 17:34
 */
public class PSDKInputBoxTextSetRequest extends BaseModel {

    /**
     * psdk 负载设备索引
     */
    @NotNull
    @Range(min = 0, max = 3)
    private Integer psdkIndex;

    /**
     * 文本内容
     */
    @NotBlank
    private String value;

    @Override
    public String toString() {
        return "PSDKInputBoxTextSetRequest{" +
                "psdkIndex=" + psdkIndex +
                ", value='" + value + '\'' +
                '}';
    }

    public Integer getPsdkIndex() {
        return psdkIndex;
    }

    public PSDKInputBoxTextSetRequest setPsdkIndex(Integer psdkIndex) {
        this.psdkIndex = psdkIndex;
        return this;
    }

    public String getValue() {
        return value;
    }

    public PSDKInputBoxTextSetRequest setValue(String value) {
        this.value = value;
        return this;
    }
}
