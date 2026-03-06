package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;

import java.util.Objects;

/**
 * 视频存储设置存储
 *
 * @author Qfei
 * @date 2026/3/6 14:54
 */
public class VideoStorageSetImpl extends PayloadCommandsHandler {

    VideoStorageSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getVideoStorageSettings());
    }
}
