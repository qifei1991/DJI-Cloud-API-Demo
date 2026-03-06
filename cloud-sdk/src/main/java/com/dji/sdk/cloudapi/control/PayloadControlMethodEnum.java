package com.dji.sdk.cloudapi.control;

import com.dji.sdk.common.BaseModel;
import com.dji.sdk.exception.CloudSDKException;

import java.util.Arrays;

/**
 * @author sean
 * @version 1.4
 * @date 2023/3/2
 */
public enum PayloadControlMethodEnum {

    CAMERA_MODE_SWITCH(ControlMethodEnum.CAMERA_MODE_SWITCH, CameraModeSwitchRequest.class),

    CAMERA_PHOTO_TAKE(ControlMethodEnum.CAMERA_PHOTO_TAKE, CameraPhotoTakeRequest.class),

    CAMERA_PHOTO_STOP(ControlMethodEnum.CAMERA_PHOTO_STOP, CameraPhotoStopRequest.class),

    CAMERA_RECORDING_START(ControlMethodEnum.CAMERA_RECORDING_START, CameraRecordingStartRequest.class),

    CAMERA_RECORDING_STOP(ControlMethodEnum.CAMERA_RECORDING_STOP, CameraRecordingStopRequest.class),

    CAMERA_AIM(ControlMethodEnum.CAMERA_AIM, CameraAimRequest.class),

    CAMERA_FOCAL_LENGTH_SET(ControlMethodEnum.CAMERA_FOCAL_LENGTH_SET, CameraFocalLengthSetRequest.class),

    GIMBAL_RESET(ControlMethodEnum.GIMBAL_RESET, GimbalResetRequest.class),

    CAMERA_LOOK_AT(ControlMethodEnum.CAMERA_LOOK_AT, CameraLookAtRequest.class),

    CAMERA_SCREEN_SPLIT(ControlMethodEnum.CAMERA_SCREEN_SPLIT, CameraScreenSplitRequest.class),

    PHOTO_STORAGE_SET(ControlMethodEnum.PHOTO_STORAGE_SET, PhotoStorageSetRequest.class),

    VIDEO_STORAGE_SET(ControlMethodEnum.VIDEO_STORAGE_SET, VideoStorageSetRequest.class),

    CAMERA_EXPOSURE_SET(ControlMethodEnum.CAMERA_EXPOSURE_SET, CameraExposureSetRequest.class),

    CAMERA_EXPOSURE_MODE_SET(ControlMethodEnum.CAMERA_EXPOSURE_MODE_SET, CameraExposureModeSetRequest.class),

    CAMERA_FOCUS_MODE_SET(ControlMethodEnum.CAMERA_FOCUS_MODE_SET, CameraFocusModeSetRequest.class),

    CAMERA_FOCUS_VALUE_SET(ControlMethodEnum.CAMERA_FOCUS_VALUE_SET, CameraFocusValueSetRequest.class),

    IR_METERING_MODE_SET(ControlMethodEnum.IR_METERING_MODE_SET, IrMeteringModeSetRequest.class),

    IR_METERING_POINT_SET(ControlMethodEnum.IR_METERING_POINT_SET, IrMeteringPointSetRequest.class),

    IR_METERING_AREA_SET(ControlMethodEnum.IR_METERING_AREA_SET, IrMeteringAreaSetRequest.class),

    CAMERA_POINT_FOCUS_ACTION(ControlMethodEnum.CAMERA_POINT_FOCUS_ACTION, CameraPointFocusActionRequest.class),

    DRC_CAMERA_ISO_SET(ControlMethodEnum.DRC_CAMERA_ISO_SET, DrcCameraIsoSetRequest.class),

    DRC_CAMERA_APERTURE_VALUE_SET(ControlMethodEnum.DRC_CAMERA_APERTURE_VALUE_SET, DrcCameraApertureValueSetRequest.class),

    DRC_CAMERA_SHUTTER_SET(ControlMethodEnum.DRC_CAMERA_SHUTTER_SET, DrcCameraShutterSetRequest.class),

    DRC_CAMERA_MECHANICAL_SHUTTER_SET(ControlMethodEnum.DRC_CAMERA_MECHANICAL_SHUTTER_SET, DrcCameraMechanicalShutterSetRequest.class),

    DRC_CAMERA_NIGHT_MODE_SET(ControlMethodEnum.DRC_CAMERA_NIGHT_MODE_SET, DrcCameraNightModeSetRequest.class),

    DRC_CAMERA_DENOISE_LEVEL_SET(ControlMethodEnum.DRC_CAMERA_DENOISE_LEVEL_SET, DrcCameraDenoiseLevelSetRequest.class),

    DRC_CAMERA_NIGHT_VISION_ENABLE(ControlMethodEnum.DRC_CAMERA_NIGHT_VISION_ENABLE, DrcCameraNightVisionEnableRequest.class),

    DRC_INFRARED_FILL_LIGHT_ENABLE(ControlMethodEnum.DRC_INFRARED_FILL_LIGHT_ENABLE, DrcInfraredFillLightEnableRequest.class),

    DRC_CAMERA_DEWARPING_SET(ControlMethodEnum.DRC_CAMERA_DEWARPING_SET, DrcCameraDewarpingSetRequest.class),

    DRC_STEALTH_STATE_SET(ControlMethodEnum.DRC_STEALTH_STATE_SET, DrcStealthStateSetRequest.class),

    DRC_NIGHT_LIGHTS_STATE_SET(ControlMethodEnum.DRC_NIGHT_LIGHTS_STATE_SET, DrcNightLightsStateSetRequest.class),

    DRC_CAMERA_PHOTO_FORMAT_SET(ControlMethodEnum.DRC_CAMERA_PHOTO_FORMAT_SET, DrcCameraPhotoFormatSetRequest.class),

    DRC_VIDEO_RESOLUTION_SET(ControlMethodEnum.DRC_VIDEO_RESOLUTION_SET, DrcVideoResolutionSetRequest.class),

    DRC_LINKAGE_ZOOM_SET(ControlMethodEnum.DRC_LINKAGE_ZOOM_SET, DrcLinkageZoomSetRequest.class),

    DRC_INTERVAL_PHOTO_SET(ControlMethodEnum.DRC_INTERVAL_PHOTO_SET, DrcIntervalPhotoSetRequest.class),

    ;

    private final ControlMethodEnum payloadMethod;

    private final Class<? extends BaseModel> clazz;

    PayloadControlMethodEnum(ControlMethodEnum payloadMethod, Class<? extends BaseModel> clazz) {
        this.payloadMethod = payloadMethod;
        this.clazz = clazz;
    }

    public ControlMethodEnum getPayloadMethod() {
        return payloadMethod;
    }

    public Class<? extends BaseModel> getClazz() {
        return clazz;
    }

    public static PayloadControlMethodEnum find(String method) {
        return Arrays.stream(values()).filter(methodEnum -> methodEnum.payloadMethod.getMethod().equals(method)).findAny()
            .orElseThrow(() -> new CloudSDKException(PayloadControlMethodEnum.class, method));
    }
}
