package com.dji.sample.interconnection.service;

import com.dji.sdk.cloudapi.device.PsdkWidget;

import java.util.List;
import java.util.Optional;

/**
 * @author Qfei
 * @date 2025/8/8 16:30
 */
public interface IPsdkWidgetRedisService {

    void setPsdkWidgetValues(String deviceSn, List<Object> psdkWidgetValues);

    Optional<List<PsdkWidget>> getPsdkWidgetValues(String deviceSn);

    boolean deletePsdkWidgetValues(String deviceSn);
}
