package com.dji.sample.control.model.param;

import com.dji.sdk.cloudapi.control.CameraTypeEnum;
import com.dji.sdk.cloudapi.control.FocusModeEnum;
import com.dji.sdk.cloudapi.control.GimbalResetModeEnum;
import com.dji.sdk.cloudapi.control.MeteringModeEnum;
import com.dji.sdk.cloudapi.device.CameraModeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author sean
 * @version 1.4
 * @date 2023/3/1
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DronePayloadParam extends AuthorityBaseParam {

    @NotNull
    @Pattern(regexp = "\\d+-\\d+-\\d+")
    private String payloadIndex;

    private CameraTypeEnum cameraType;

    @Range(min = 2, max = 200)
    private Float zoomFactor;

    private CameraModeEnum cameraMode;

    /**
     * Whether the relative location of drone head and gimbal is locked
     * true: Lock the gimbal, the gimbal and the drone rotate together.
     * false: Only the gimbal rotates, but the drone does not.
     */
    private Boolean locked;

    private Double pitchSpeed;

    /**
     * Only valid when locked is false.
     */
    private Double yawSpeed;

    /**
     * upper left corner as center point
     */
    @Range(min = 0, max = 1)
    private Double x;

    @Range(min = 0, max = 1)
    private Double y;

    private GimbalResetModeEnum resetMode;

    /**
     * 测温模式 {"0":"关闭测温","1":"点测温","2":"区域测温"}
     */
    private MeteringModeEnum mode;

    /**
     * 测温区域宽度 {"max":1,"min":0}
     */
    @Range(min = 0, max = 1)
    private Double width;

    /**
     * 1.测温区域高度 {"max":1,"min":0}
     * 2. LookAt高度 Ellipsoid height
     */
    private Double height;

    /**
     * 对焦模式 {"0":"MF","1":"AFS","2":"AFC"}
     */
    private FocusModeEnum focusMode;

    /**
     * 是否使能分屏
     */
    private Boolean enable;

    /**
     * The latitude of target point is angular values.
     * Negative values for south latitude and positive values for north latitude.
     * It is accurate to six decimal places.
     */
    @Range(min = -90, max = 90)
    private Float latitude;

    /**
     * The latitude of target point is angular values.
     * Negative values for west longitude and positive values for east longitude.
     * It is accurate to six decimal places.
     */
    @Range(min = -180, max = 180)
    private Float longitude;

}
