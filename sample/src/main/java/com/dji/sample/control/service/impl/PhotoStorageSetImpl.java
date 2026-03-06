package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;

import java.util.Objects;

/**
 * 照片存储设置
 *
 * @author Qfei
 * @date 2026/3/6 14:53
 */
public class PhotoStorageSetImpl extends PayloadCommandsHandler {

    public PhotoStorageSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getPhotoStorageSettings());
    }
}
