package com.newshacker.service;

import com.newshacker.db.redis.PostRedis;
import com.newshacker.exception.PostCreateErrorException;
import com.newshacker.model.impl.Post;
import com.newshacker.exception.PostCreateRequestNotValidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PostService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PostRedis postRedis;

    public Post create(Post post) {
        if (post == null) {
            logger.error("Can't create post, Request is empty");
            throw new PostCreateRequestNotValidException("Request is empty");
        }
        if (post.getText() == null || post.getText().isEmpty()) {
            logger.error("Can't create post, Text is not valid");
            throw new PostCreateRequestNotValidException("Text is a must");
        }
        if (post.getUserId() == null) {
            logger.error("Can't create post, UserId is not valid");
            throw new PostCreateRequestNotValidException("User is a must");
        }
        post.setPostId(UUID.randomUUID().toString());
        post.setCreatedAt(System.currentTimeMillis());
        boolean added = postRedis.add(post);
        if (!added) {
            logger.error("Can't create post, Redis response is false");
            throw new PostCreateErrorException("Can't create post");
        }
        logger.info("Created new post {}", post.getPostId());
        return post;
    }
}
