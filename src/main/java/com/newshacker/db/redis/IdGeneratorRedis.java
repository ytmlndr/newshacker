package com.newshacker.db.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class IdGeneratorRedis {

    @Autowired
    private JedisPool jedisPool;

    public Long getNextPostId() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hincrBy("ids", "post", 1L);
        }
    }
}
