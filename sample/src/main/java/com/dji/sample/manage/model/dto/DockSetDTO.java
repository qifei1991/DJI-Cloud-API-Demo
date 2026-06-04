package com.dji.sample.manage.model.dto;

import com.dji.sdk.cloudapi.device.RainfallEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 机场设置参数
 *
 * @author Qfei
 * @date 2026/5/20 17:42
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class DockSetDTO {

    @NotBlank(message = "Dock SN can not be blank.")
    private String deviceSn;

    /**
     * 风速，单位 m/s
     */
    private Integer windSpeed = 9;

    /**
     * 雨量，枚举 {@link RainfallEnum}
     */
    private RainfallEnum rainfall = RainfallEnum.MODERATE;

    /**
     * 飞机失联上报手机号
     */
    @Pattern(regexp = "^1[3|4|5|6|7|8|9][0-9]{9}(,1[3|4|5|6|7|8|9][0-9]{9})*$", message = "手机号格式不匹配")
    private String droneLostReportPhone;

    /**
     * 返航预留电量
     */
    private Integer remainingPowerForReturnHome;

    private Long createTime;

    private String createUsername;

    private Long updateTime;

    private String updateUsername;
}
