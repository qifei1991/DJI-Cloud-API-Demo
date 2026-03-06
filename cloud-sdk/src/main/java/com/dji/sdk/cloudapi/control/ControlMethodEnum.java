package com.dji.sdk.cloudapi.control;

/**
 * @author sean
 * @version 1.4
 * @date 2023/3/2
 */
public enum ControlMethodEnum {

    FLIGHT_AUTHORITY_GRAB("flight_authority_grab"),

    PAYLOAD_AUTHORITY_GRAB("payload_authority_grab"),

    DRC_MODE_ENTER("drc_mode_enter"),

    DRC_MODE_EXIT("drc_mode_exit"),

    FLY_TO_POINT("fly_to_point"),

    FLY_TO_POINT_STOP("fly_to_point_stop"),

    FLY_TO_POINT_UPDATE("fly_to_point_update"),

    TAKEOFF_TO_POINT("takeoff_to_point"),

    CAMERA_MODE_SWITCH("camera_mode_switch"),

    CAMERA_PHOTO_TAKE("camera_photo_take"),

    CAMERA_PHOTO_STOP("camera_photo_stop"),

    CAMERA_RECORDING_START("camera_recording_start"),

    CAMERA_RECORDING_STOP("camera_recording_stop"),

    CAMERA_AIM("camera_aim"),

    CAMERA_FOCAL_LENGTH_SET("camera_focal_length_set"),

    GIMBAL_RESET("gimbal_reset"),

    CAMERA_LOOK_AT("camera_look_at"),

    CAMERA_SCREEN_SPLIT("camera_screen_split"),

    PHOTO_STORAGE_SET("photo_storage_set"),

    VIDEO_STORAGE_SET("video_storage_set"),

    CAMERA_EXPOSURE_SET("camera_exposure_set"),

    CAMERA_EXPOSURE_MODE_SET("camera_exposure_mode_set"),

    CAMERA_FOCUS_MODE_SET("camera_focus_mode_set"),

    CAMERA_FOCUS_VALUE_SET("camera_focus_value_set"),

    IR_METERING_MODE_SET("ir_metering_mode_set"),

    IR_METERING_POINT_SET("ir_metering_point_set"),

    IR_METERING_AREA_SET("ir_metering_area_set"),

    CAMERA_POINT_FOCUS_ACTION("camera_point_focus_action"),

    DRONE_CONTROL("drone_control"),

    DRONE_EMERGENCY_STOP("drone_emergency_stop"),

    HEART_BEAT("heart_beat"),

    POI_MODE_ENTER("poi_mode_enter"),

    POI_MODE_EXIT("poi_mode_exit"),

    POI_CIRCLE_SPEED_SET("poi_circle_speed_set"),

    DRC_CAMERA_ISO_SET("drc_camera_iso_set"),

    DRC_CAMERA_APERTURE_VALUE_SET("drc_camera_aperture_value_set"),

    DRC_CAMERA_SHUTTER_SET("drc_camera_shutter_set"),

    DRC_CAMERA_MECHANICAL_SHUTTER_SET("drc_camera_mechanical_shutter_set"),

    DRC_CAMERA_NIGHT_MODE_SET("drc_camera_night_mode_set"),

    DRC_CAMERA_DENOISE_LEVEL_SET("drc_camera_denoise_level_set"),

    DRC_CAMERA_NIGHT_VISION_ENABLE("drc_camera_night_vision_enable"),

    DRC_INFRARED_FILL_LIGHT_ENABLE("drc_infrared_fill_light_enable"),

    DRC_CAMERA_DEWARPING_SET("drc_camera_dewarping_set"),

    DRC_STEALTH_STATE_SET("drc_stealth_state_set"),

    DRC_NIGHT_LIGHTS_STATE_SET("drc_night_lights_state_set"),

    DRC_CAMERA_PHOTO_FORMAT_SET("drc_camera_photo_format_set"),

    DRC_VIDEO_RESOLUTION_SET("drc_video_resolution_set"),

    DRC_LINKAGE_ZOOM_SET("drc_linkage_zoom_set"),

    DRC_INTERVAL_PHOTO_SET("drc_interval_photo_set"),

    ;

    private final String method;

    ControlMethodEnum(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

}
