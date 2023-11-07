package com.dji.sample.component.redis;

import com.dji.sample.manage.model.enums.DeviceDomainEnum;
import lombok.NoArgsConstructor;

/**
 * @author sean
 * @version 1.0
 * @date 2022/4/21
 */
@NoArgsConstructor
public final class RedisConst {

    public static final int WAYLINE_JOB_BLOCK_TIME = 600;

    public static final String DELIMITER = ":";

    public static final Integer DEVICE_ALIVE_SECOND = 60;

    public static final Integer WEBSOCKET_ALIVE_SECOND = 60 * 60 * 24;

    public static final String ONLINE_PREFIX = "online" + DELIMITER;

    public static final String DEVICE_ONLINE_PREFIX = ONLINE_PREFIX + DeviceDomainEnum.SUB_DEVICE + DELIMITER;

    public static final String WEBSOCKET_PREFIX = "webSocket" + DELIMITER;

    public static final String WEBSOCKET_ALL = WEBSOCKET_PREFIX + "all";

    public static final String HMS_PREFIX = "hms" + DELIMITER;

    public static final String FIRMWARE_UPGRADING_PREFIX = "upgrading" + DELIMITER;

    public static final String STATE_PAYLOAD_PREFIX = "payload" + DELIMITER;

    public static final String LOGS_FILE_PREFIX = "logs_file" + DELIMITER;

    public static final String WAYLINE_JOB_PREPARED = "wayline_job_prepared";

    public static final String WAYLINE_JOB_CONDITION_PREFIX = "wayline_job_condition" + DELIMITER;

    public static final String WAYLINE_JOB_BLOCK_PREFIX = "wayline_job_block" + DELIMITER;

    public static final String WAYLINE_JOB_RUNNING_PREFIX = "wayline_job_running" + DELIMITER;

    public static final String WAYLINE_JOB_PAUSED_PREFIX = "wayline_job_paused" + DELIMITER;

    public static final String OSD_PREFIX = "osd" + DELIMITER;

    public static final String MEDIA_FILE_PREFIX = "media_file" + DELIMITER;

    public static final String MEDIA_HIGHEST_PRIORITY_PREFIX = "media_highest_priority" + DELIMITER;

    public static final String LIVE_CAPACITY = "live_capacity";

    public static final String DRC_PREFIX = "drc" + DELIMITER;

    public static final Integer DRC_MODE_ALIVE_SECOND = 3600;

    public static final String MQTT_ACL_PREFIX = "mqtt_acl" + DELIMITER;

    public static final String FILE_UPLOADING_PREFIX = "file_uploading" + DELIMITER;

    public static final String DRONE_CONTROL_PREFiX = "control_source" + DELIMITER;

    public static final String WAYLINE_JOB_BREAKPOINT_PREFIX = "wayline_job_breakpoint" + DELIMITER;

    public static final String DRC_AUTHORITY_PREFIX = DRC_PREFIX  + "authority" + DELIMITER;
}
