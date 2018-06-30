package com.newshacker.db.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

public class AbstractRedisService {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected JedisPool jedisPool;
}
