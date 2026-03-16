package com.dji.sample.control.model.param;

import com.dji.sample.control.model.enums.CameraModeSetEnum;
import com.dji.sdk.cloudapi.control.*;
import com.dji.sdk.cloudapi.device.CameraIsoEnum;
import com.dji.sdk.cloudapi.device.CameraModeEnum;
import com.dji.sdk.cloudapi.device.ShutterSpeedEnum;
import com.dji.sdk.cloudapi.device.SwitchActionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @author sean
 * @version 1.4
 * @date 2023/3/1
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
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

    /**
     * 云台重置模式 {@link GimbalResetModeEnum}
     */
    private GimbalResetModeEnum resetMode;

    /**
     * 测温模式 {@link MeteringModeEnum} {"0":"关闭测温","1":"点测温","2":"区域测温"}
     * 夜景模式 {@link CameraNightModeEnum}{"0":"关闭","1":"开启","2":"自动"}
     */
    private CameraModeSetEnum mode;

    /**
     * 1. 测温区域宽度 {"max":1,"min":0}
     * 2. LookAt高度 Ellipsoid width
     * 3. 框选变焦区域宽度
     */
    @Range(min = 0, max = 1)
    private Double width;

    /**
     * 1.测温区域高度 {"max":1,"min":0}
     * 2. LookAt高度 Ellipsoid height
     * 3. 框选变焦区域高度
     */
    private Double height;

    /**
     * 对焦模式 {"0":"MF","1":"AFS","2":"AFC"}
     */
    private FocusModeEnum focusMode;

    /**
     * 是否使能
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

    /**
     * <pre>
     * The argument of Setting CameraFocusValue.
     * cameraType: {@link ExposureCameraTypeEnum}
     * focusValue: {@link Integer}
     * </pre>
     */
    private Integer focusValue;

    /**
     * <pre>
     * The argument of Setting CameraExposureValue.
     * cameraType: {@link ExposureCameraTypeEnum}
     * exposureValue: {@link ExposureValueEnum}
     * </pre>
     */
    private ExposureValueEnum exposureValue;

    /**
     * <pre>
     * The argument of Setting CameraExposureMode.
     * cameraType: {@link ExposureCameraTypeEnum}
     * exposureMode: {@link ExposureModeEnum}
     * </pre>
     */
    private ExposureModeEnum exposureMode;

    /**
     * <pre>
     * The argument of Setting DrcCameraIso.
     * cameraType: {@link ExposureCameraTypeEnum}
     * isoValue: {@link CameraIsoEnum}
     * </pre>
     */
    private CameraIsoEnum isoValue;

    /**
     * <pre>
     * The argument of Setting DrcCameraAperture.
     * cameraType: {@link ExposureCameraTypeEnum}
     * apertureValue: {@link CameraApertureEnum}
     * </pre>
     */
    private CameraApertureEnum apertureValue;

    /**
     * <pre>
     * The argument of Setting DrcCameraShutter.
     * cameraType: {@link ExposureCameraTypeEnum}
     * shutterValue: {@link ShutterSpeedEnum}
     * </pre>
     */
    private ShutterSpeedEnum shutterValue;

    /**
     * <pre>
     * The argument of Setting DrcCameraMechanicalShutterState.
     * cameraType: {@link ExposureCameraTypeEnum}
     * mechanicalShutterState: {@link MechanicalShutterStateEnum}
     * </pre>
     */
    private MechanicalShutterStateEnum mechanicalShutterState;

    /**
     * 夜视降噪等级
     */
    private CameraDenoiseLevelEnum level;

    /**
     * 镜头去畸变
     * <pre>
     * The argument of Setting DrcCameraDewarpingState.
     * cameraType: {@link CameraTypeEnum#WIDE}
     * dewarpingState: {@link SwitchActionEnum}
     * </pre>
     */
    private SwitchActionEnum dewarpingState;

    /**
     * 隐蔽模式
     */
    private SwitchActionEnum stealthState;

    /**
     * 夜航灯设置参数
     */
    private SwitchActionEnum nightLightsState;

    /**
     * 红外照片格式设置
     */
    private IrPhotoFormatEnum photoFormat;

    /**
     * 视频分辨率设置
     */
    private VideoResolutionEnum videoResolution;

    /**
     * Photo storage type. Multi-selection.
     */
    private List<LensStorageSettingsEnum> photoStorageSettings;

    /**
     * Video storage type. Multi-selection.
     */
    private List<LensStorageSettingsEnum> videoStorageSettings;

    /**
     * 状态设置
     * <pre>
     *    1. 红外联动变焦：{@link SwitchActionEnum}
     * </pre>
     */
    private SwitchActionEnum state;

    /**
     * 拍照间隔
     */
    private PhotoIntervalEnum interval;
}
