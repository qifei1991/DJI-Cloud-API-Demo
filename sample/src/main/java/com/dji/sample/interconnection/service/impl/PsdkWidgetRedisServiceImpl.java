package com.dji.sample.interconnection.service.impl;

import com.dji.sample.component.redis.RedisConst;
import com.dji.sample.component.redis.RedisOpsUtils;
import com.dji.sample.interconnection.service.IPsdkWidgetRedisService;
import com.dji.sdk.cloudapi.device.PsdkWidget;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Qfei
 * @date 2025/8/8 16:41
 */
@Service
public class PsdkWidgetRedisServiceImpl implements IPsdkWidgetRedisService {

    @Override
    public void setPsdkWidgetValues(String deviceSn, List<PsdkWidget> psdkWidgetValues) {
        RedisOpsUtils.hashSet(RedisConst.PSDK_WIDGET_VALUES, deviceSn, psdkWidgetValues);
    }

    @Override
    public Optional<List<PsdkWidget>> getPsdkWidgetValues(String deviceSn) {
        return Optional.ofNullable((List<PsdkWidget>) RedisOpsUtils.hashGet(RedisConst.PSDK_WIDGET_VALUES, deviceSn));
    }

    @Override
    public boolean deletePsdkWidgetValues(String deviceSn) {
        return RedisOpsUtils.hashDel(RedisConst.PSDK_WIDGET_VALUES, new String[]{ deviceSn });
    }
}
