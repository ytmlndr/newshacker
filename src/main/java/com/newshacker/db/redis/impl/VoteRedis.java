package com.newshacker.db.redis.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newshacker.model.impl.Vote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
}
