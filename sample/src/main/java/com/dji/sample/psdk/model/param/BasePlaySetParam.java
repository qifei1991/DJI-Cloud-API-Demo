package com.dji.sample.psdk.model.param;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 播放参数设置基类
 *
 * @author Qfei
 * @date 2026/3/19 16:52
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class BasePlaySetParam {

    /**
     * 机场设备SN
     */
    @NotBlank(message = "设备SN不能为空")
    protected String deviceSn;

    @Min(0)
    @Max(3)
    protected Integer psdkIndex;

    @Override
    public String toString() {
        return "BasePlaySetParam{" +
                "deviceSn='" + deviceSn + '\'' +
                ", psdkIndex=" + psdkIndex +
                '}';
    }
}
