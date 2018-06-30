package com.newshacker.db.redis.impl;

import com.newshacker.db.redis.AbstractRedisService;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component
public class IdGeneratorRedis extends AbstractRedisService {

    public Long getNextPostId() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hincrBy("ids", "post", 1L);
        }
    }
}
