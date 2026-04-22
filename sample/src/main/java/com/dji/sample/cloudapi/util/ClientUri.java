package com.dji.sample.cloudapi.util;

import lombok.NoArgsConstructor;

/**
 * 客户端请求URI地址
 *
 * @author Qfei
 * @date 2022/12/19 13:48
 */
@NoArgsConstructor
public class ClientUri {

    public static final String URI_DEVICE_ONLINE = "/devices/online";
    public static final String URI_DEVICE_OFFLINE = "/devices/offline";
    public static final String URI_OSD_STATE = "/devices/osd/{category}";
    public static final String URI_SORTIES_START = "/sorties/start";
    public static final String URI_SORTIES_COMPLETE = "/sorties/complete";
    public static final String URI_SORTIES_CANCEL = "/sorties/cancel";
    public static final String URI_SORTIES_UPLOAD = "/sorties/upload";
    public static final String URI_SORTIES_PROGRESS = "/sorties/{sorties_id}/progress";
    public static final String URI_MEDIA_PROGRESS = "/media/{sorties_id}/progress";
    public static final String URI_MEDIA_UPLOAD_CALLBACK = "/media/upload-callback";
    public static final String URI_MEDIA_RC_UPLOAD_CALLBACK = "/media/rc/upload-callback";
    public static final String URI_WAYLINE_REPORT = "/wayline/file/upload-report";
    public static final String URI_TAKEOFF_TO_PROGRESS = "/flight-task/takeoff-to-progress";
    public static final String URI_IN_FLIGHT_WAYLINE_PROGRESS = "/flight-task/in-flight-wayline-progress";
    public static final String URI_RETURN_HOME_FAIL = "/flight-task/return-home/fail";

    public static final String URI_RECEIVE = "/tst/receive/{0}";
}
