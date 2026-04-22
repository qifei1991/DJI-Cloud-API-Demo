package com.dji.sample.control.service.impl;

import com.dji.sample.component.redis.RedisConst;
import com.dji.sample.component.redis.RedisOpsUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Qfei
 * @date 2026/4/20 11:31
 */
@Component
public class DrcRedisService {

    public void setDrcHeartBeat(String Sn, String taskId) {
        RedisOpsUtils.set(RedisConst.DRC_HEART_BEAT_PREFIX + Sn, taskId);
    }

    public Optional<String> getDrcHeartBeat(String Sn) {
        return Optional.ofNullable((String) RedisOpsUtils.get(RedisConst.DRC_HEART_BEAT_PREFIX + Sn));
    }

    public Boolean deleteDrcHeartBeat(String Sn) {
        return RedisOpsUtils.del(RedisConst.DRC_HEART_BEAT_PREFIX + Sn);
    }
}
