package com.newshacker.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ComponentScan("com.newshacker.db.redis")
public class RedisConfig {

    @Bean("jedis.config")
    public JedisPoolConfig jedisConnectionFactory(
            @Value("${jedis.pool.min-idle}") int minIdle,
            @Value("${jedis.pool.max-idle}") int maxIdle,
            @Value("${jedis.pool.block-when-exhausted}") boolean blockWhenExhausted,
            @Value("${jedis.pool.min-idle}") int maxTotal) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setBlockWhenExhausted(blockWhenExhausted);
        jedisPoolConfig.setMaxTotal(maxTotal);
        return jedisPoolConfig;

    }

    @Bean
    public JedisPool jedisPool(@Qualifier("jedis.config") JedisPoolConfig jedisPoolConfig,
                               @Value("${jedis.host}") String host,
                               @Value("${jedis.port}") Integer port) {
        return new JedisPool(jedisPoolConfig, host, port);
    }
}
