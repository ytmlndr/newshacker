package com.newshacker.db.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newshacker.model.impl.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.Optional;

@Service
public class PostRedis {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JedisPool jedisPool;

    public boolean add(Post post) {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = post.toJson();
            Transaction multi = jedis.multi();
            // Store post in a redis hashmap
            Response<Long> setResponse = multi.hset("posts", post.getPostId().toString(), json);
            // Store post reference in zset
            // Sort posts by upvotes and createdAt
            String scoreString = String.format("0.%d", post.getCreatedAt());
            double score = Double.parseDouble(scoreString);
            Response<Long> zsetResponse = multi.zadd("posts.sort", score, post.getPostId().toString());
            multi.exec();
            return setResponse.get().equals(1L) && zsetResponse.get().equals(1L);
        } catch (JsonProcessingException e) {
            logger.error("Can't parse post to json", e);
            return false;
        }
    }

}
