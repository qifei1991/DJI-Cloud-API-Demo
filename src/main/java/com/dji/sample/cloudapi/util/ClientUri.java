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
    public static final String URI_OSD_STATE = "/devices/{category}";
    public static final String URI_SORTIES_START = "/sorties/start";
    public static final String URI_SORTIES_STOP = "/sorties/stop";
    public static final String URI_SORTIES_UPLOAD = "/sorties/upload";
    public static final String URI_SORTIES_PROGRESS = "/sorties/{sortiesId}/progress";
    public static final String URI_MEDIA_PROGRESS = "/media/{sortiesId}/upload-progress";
    public static final String URI_MEDIA_UPLOAD_CALLBACK = "/media/upload-callback";
}