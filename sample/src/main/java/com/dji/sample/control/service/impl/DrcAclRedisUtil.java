package com.dji.sample.control.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Qfei
 * @date 2025/4/16 9:30
 */
@Component
public class DrcAclRedisUtil {

    public static RedisTemplate<String, Object> redisTemplateForJson;

    @Autowired
    @Qualifier("redisTemplateForJson")
    public void setRedisTemplateForJson(RedisTemplate<String, Object> redisTemplateForJson) {
        DrcAclRedisUtil.redisTemplateForJson = redisTemplateForJson;
    }

    /**
     * HSET
     * @param key
     * @param field
     * @param value
     */
    public static void hashSet(String key, String field, Object value) {
        redisTemplateForJson.opsForHash().put(key, field, value);
    }
}
