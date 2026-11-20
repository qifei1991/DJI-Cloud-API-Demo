package com.dji.sample.wayline.model.dto;

/**
 * @author sean
 * @version 1.3
 * @date 2022/10/27
 */
public class KmzFileProperties {

    private KmzFileProperties() {

    }

    public static final String WAYLINE_FILE_SUFFIX = ".kmz";

    public static final String FILE_DIR_FIRST = "wpmz";

    public static final String FILE_DIR_SECOND_RES = "res";

    public static final String FILE_DIR_SECOND_TEMPLATE = "template.kml";

    public static final String FILE_DIR_SECOND_WAYLINES = "waylines.wpml";

    public static final String TAG_WPML_PREFIX = "wpml:";

    public static final String TAG_DRONE_INFO = "droneInfo";

    public static final String TAG_DRONE_ENUM_VALUE = "droneEnumValue";

    public static final String TAG_DRONE_SUB_ENUM_VALUE = "droneSubEnumValue";

    public static final String TAG_PAYLOAD_INFO = "payloadInfo";

    public static final String TAG_PAYLOAD_ENUM_VALUE = "payloadEnumValue";

    public static final String TAG_PAYLOAD_SUB_ENUM_VALUE = "payloadSubEnumValue";

    public static final String TAG_TEMPLATE_TYPE = "templateType";

    public static final String TAG_MISSION_CONFIG = "missionConfig";

    /**
     * Whether to exit the wayline and execute the rc lost action after the aircraft loses contact.
     */
    public static final String TAG_EXIT_ON_RC_LOST = "exitOnRCLost";

    /**
     * The action of the aircraft after it loses contact.
     */
    public static final String TAG_EXECUTE_RC_LOST_ACTION = "executeRCLostAction";

    public static final String EXIT_ON_RC_LOST_CONTINUE = "goContinue";

    public static final String EXIT_ON_RC_LOST_EXECUTE_ACTION = "executeLostAction";

    public static final String RC_LOST_ACTION_GO_BACK = "goBack";

    public static final String RC_LOST_ACTION_HOVER = "hover";

    public static final String RC_LOST_ACTION_LANDING = "landing";

    /**
     * The directory in the bucket which stores the wayline file copies.
     */
    public static final String WAYLINE_FILE_COPY_DIR = "wayline_file_copy";
}
