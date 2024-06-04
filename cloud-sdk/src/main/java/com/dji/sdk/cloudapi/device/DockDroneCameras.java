package com.dji.sdk.cloudapi.device;

import com.dji.sdk.common.BaseModel;

import java.util.List;

/**
 * @author Qfei
 * @date 2024/5/29 10:24
 */
public class DockDroneCameras extends BaseModel {

    private List<OsdCamera> cameras;

    @Override
    public String toString() {
        return "DockDroneCameras{" +
                "cameras=" + cameras +
                '}';
    }

    public List<OsdCamera> getCameras() {
        return cameras;
    }

    public DockDroneCameras setCameras(List<OsdCamera> cameras) {
        this.cameras = cameras;
        return this;
    }
}
