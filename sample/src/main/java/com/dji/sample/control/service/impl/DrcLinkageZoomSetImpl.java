package com.dji.sample.control.service.impl;

import com.dji.sample.control.model.param.DronePayloadParam;

import java.util.Objects;

/**
 * 联动变焦
 * <pre>红外联动变焦仅支持 Matrice 3TD 机型</pre>
 *
 * @author Qfei
 * @date 2026/3/6 16:08
 */
public class DrcLinkageZoomSetImpl extends PayloadCommandsHandler {

    DrcLinkageZoomSetImpl(DronePayloadParam param) {
        super(param);
    }

    @Override
    public boolean valid() {
        return Objects.nonNull(param.getState());
    }
}
