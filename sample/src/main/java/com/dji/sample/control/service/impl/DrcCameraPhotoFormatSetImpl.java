package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;

import java.util.Objects;

/**
 * 红外照片格式设置
 * <pre>用于设置红外照片格式，需要直播镜头先切换为红外才可有效使用</pre>
 *
 * @author Qfei
 * @date 2026/3/6 14:05
 */
public class DrcCameraPhotoFormatSetImpl extends PayloadCommandsHandler {

    DrcCameraPhotoFormatSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getPayloadIndex()) && Objects.nonNull(param.getPhotoFormat());
    }
}
