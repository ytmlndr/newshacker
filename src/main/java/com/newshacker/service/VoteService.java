package com.newshacker.service;

import com.newshacker.db.redis.impl.VoteRedis;
import com.newshacker.exception.VoteCreateRequestNotValidException;
import com.newshacker.exception.VoteCreateRequestUserCreatedPostVoteException;
import com.newshacker.exception.VoteCreateRequestUserVotedPostException;
import com.newshacker.model.impl.Post;
import com.newshacker.model.impl.Vote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private VoteRedis voteRedis;

    @Autowired
    private PostService postService;

    public Vote create(Vote vote) {
        if (vote.getUserId() == null || vote.getVoteType() == null) {
            logger.error("Can't create vote, bad request");
            throw new VoteCreateRequestNotValidException("Vote request is not valid");
        }
        Optional<Post> postOptional = postService.read(vote.getPostId());
        if (!postOptional.isPresent()) {
            logger.error("Can't create vote, post {} does not exists", vote.getUserId(), vote.getPostId());
            throw new VoteCreateRequestUserVotedPostException("Post does not exists");
        }
        if (vote.getUserId().equals(postOptional.get().getUserId())) {
            logger.error("Can't create vote, user try to vote his own post", vote.getUserId(), vote.getPostId());
            throw new VoteCreateRequestUserCreatedPostVoteException("User can't vote his own post");
        }
        boolean userVotedPost = voteRedis.isUserVotedPost(vote.getPostId(), vote.getUserId());
        if (userVotedPost) {
            logger.error("Can't create vote, user {} voted post {}", vote.getUserId(), vote.getPostId());
            throw new VoteCreateRequestUserVotedPostException("User voted post");
        }
        vote.setVoteId(String.format("%s_%d_%d", vote.getVoteType().lowercase(), vote.getPostId(), vote.getUserId()));
        vote.setCreatedAt(System.currentTimeMillis());
        boolean voteCreated = voteRedis.add(vote);
        if (!voteCreated) {
            logger.error("Can't create vote, bad request");
            throw new VoteCreateRequestUserVotedPostException("User voted post");
        }
        logger.info("Created new vote for post {}", vote.getPostId());
        return vote;
    }
}
