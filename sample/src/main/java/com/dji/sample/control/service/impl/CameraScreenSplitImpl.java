package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author Qfei
 * @date 2025/7/31 15:52
 */
public class CameraScreenSplitImpl extends PayloadCommandsHandler {

    private final Logger logger = LoggerFactory.getLogger(CameraScreenSplitImpl.class);

    CameraScreenSplitImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getPayloadIndex()) && Objects.nonNull(param.getEnable());
    }

    @Override
    public boolean canPublish(String deviceSn) {
        super.canPublish(deviceSn);
        logger.info("- current screen split enable: {}", osdCamera.getScreenSplitEnable());
        return !osdCamera.getScreenSplitEnable().equals(param.getEnable());
    }
}
