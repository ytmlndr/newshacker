package com.newshacker.db.redis.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newshacker.model.impl.Vote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.util.*;

@Service
public class VoteRedis {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String postsUsersVoteKeyFormat = "post:%d:%s:users";

    @Autowired
    private JedisPool jedisPool;

    public boolean add(Vote vote) {
        try (Jedis jedis = jedisPool.getResource()) {
            String postUsersVoteKey = String.format(postsUsersVoteKeyFormat, vote.getPostId(), vote.getVoteType().lowercase());
            Transaction multi = jedis.multi();
            Response<Long> sadd = multi.sadd(postUsersVoteKey, vote.getUserId().toString());
            Response<Long> hset = multi.hset(vote.getVoteType().lowercase(), vote.getVoteId(), new ObjectMapper().writeValueAsString(vote));
            long incrementPostScoreValue = vote.getVoteType().incrementValue;
            Response<Double> zincrby = multi.zincrby("posts.sort", incrementPostScoreValue, vote.getPostId().toString());
            multi.exec();
            return Optional.ofNullable(sadd.get()).orElse(0L).equals(1L) &&
                    Optional.ofNullable(hset.get()).orElse(0L).equals(1L) &&
                    Optional.ofNullable(zincrby.get()).isPresent();
        } catch (JsonProcessingException e) {
            logger.error("Can't parse vote", e);
            return false;
        }
    }

    public boolean isUserVotedPost(Long postId, Integer userId) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<Response<Boolean>> responses = new ArrayList<>();
            Pipeline pipelined = jedis.pipelined();
            for (Vote.VoteType voteType : Vote.VoteType.values()) {
                String postUsersVoteKey = String.format(postsUsersVoteKeyFormat, postId, voteType.lowercase());
                Response<Boolean> response = pipelined.sismember(postUsersVoteKey, userId.toString());
                responses.add(response);
            }
            pipelined.sync();
            for (Response<Boolean> response : responses) {
                if (response.get()) {
                    return true;
                }
            }
            return false;
        }
    }

    public Map<Long, Map<Vote.VoteType, Long>> read(List<Long> postIds) {
        Map<Long, Map<Vote.VoteType, Response<Long>>> responses = new HashMap<>();
        try (Jedis jedis = jedisPool.getResource()) {
            Pipeline pipelined = jedis.pipelined();
            for (Long postId : postIds) {
                for (Vote.VoteType voteType : Vote.VoteType.values()) {
                    String key = String.format(postsUsersVoteKeyFormat, postId, voteType.lowercase());
                    Response<Long> response = pipelined.scard(key);
                    responses.putIfAbsent(postId, new HashMap<>());
                    responses.get(postId).put(voteType, response);
                }
            }
            pipelined.sync();
            Map<Long, Map<Vote.VoteType, Long>> result = new HashMap<>();
            for (Long postId : responses.keySet()) {
                Map<Vote.VoteType, Response<Long>> voteTypeResponseMap = responses.get(postId);
                for (Map.Entry<Vote.VoteType, Response<Long>> voteTypeResponseEntry : voteTypeResponseMap.entrySet()) {
                    Vote.VoteType voteType = voteTypeResponseEntry.getKey();
                    Response<Long> response = voteTypeResponseEntry.getValue();
                    result.putIfAbsent(postId, new HashMap<>());
                    result.get(postId).put(voteType, response.get());
                }
            }
            return result;
        }
    }
}
