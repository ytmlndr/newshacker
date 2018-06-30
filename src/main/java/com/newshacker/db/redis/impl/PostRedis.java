package com.newshacker.db.redis.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newshacker.db.redis.AbstractRedisService;
import com.newshacker.model.impl.Post;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PostRedis extends AbstractRedisService {

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

    public Optional<Post> read(Long postId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String postJson = jedis.hget("posts", postId.toString());
            if (postJson == null) {
                return Optional.empty();
            }
            Post post = new ObjectMapper().readValue(postJson, Post.class);
            return Optional.ofNullable(post);
        } catch (IOException e) {
            logger.error("Can't parse string to post", e);
            return Optional.empty();
        }
    }

    public boolean update(Post post) {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = new ObjectMapper().writeValueAsString(post);
            jedis.hset("posts", post.getPostId().toString(), json);
            return true;
        } catch (IOException e) {
            logger.error("Can't parse string to post", e);
            return false;
        }
    }

    public List<Post> readDesc(int size) {
        try (Jedis jedis = jedisPool.getResource()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Set<String> postsIds = jedis.zrevrange("posts.sort", 0, size);
            Pipeline pipelined = jedis.pipelined();
            List<Response<String>> responses = new ArrayList<>();
            for (String postId : postsIds) {
                responses.add(pipelined.hget("posts", postId));
            }
            pipelined.sync();
            List<Post> posts = new ArrayList<>();
            try {
                for (Response<String> response : responses) {
                    posts.add(objectMapper.readValue(response.get(), Post.class));
                }
            } catch (IOException e) {
                logger.error("Can't parse value to Post", e);
            }
            return posts;
        }
    }
}
