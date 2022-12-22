package com.dji.sample.cloudapi.model.param;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 设备下线参数
 *
 * @author Qfei
 * @date 2022/11/23 11:57
 */
@Data
@Builder
public class DeviceOfflineParam {
    @NotBlank(message = "设备SN码不能为空")
    private String sn;
    @Builder.Default
    private String causeCode = "1";
    @NotBlank(message = "下线时间不能为空")
    @Pattern(regexp = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$",
            message = "时间格式应为：yyyy-MM-dd HH:mm:ss")
    private String time;
}
