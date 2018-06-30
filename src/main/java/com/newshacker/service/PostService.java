package com.newshacker.service;

import com.newshacker.db.redis.impl.IdGeneratorRedis;
import com.newshacker.db.redis.impl.PostRedis;
import com.newshacker.exception.*;
import com.newshacker.model.impl.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class PostService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PostRedis postRedis;

    @Autowired
    private IdGeneratorRedis idGeneratorRedis;

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
        post.setPostId(idGeneratorRedis.getNextPostId());
        post.setCreatedAt(System.currentTimeMillis());
        boolean added = postRedis.add(post);
        if (!added) {
            logger.error("Can't create post, Redis response is false");
            throw new PostCreateErrorException("Can't create post");
        }
        logger.info("Created new post {}", post.getPostId());
        return post;
    }

    public Post update(Long postId, Post post) {
        if (StringUtils.isEmpty(post.getText())) {
            logger.error("Can't update post, Text is not valid");
            throw new PostUpdateRequestNotValidException("Text is a must");
        }
        if (StringUtils.isEmpty(post.getUserId())) {
            logger.error("Can't update post, UserId is not valid");
            throw new PostUpdateRequestNotValidException("UserId is a must");
        }
        Optional<Post> postOptional = postRedis.read(postId);
        if (!postOptional.isPresent()) {
            logger.error("Can't update post, Post does not exists");
            throw new PostUpdateRequestPostNotExistsException("Post does not exists");
        }
        Post fullPost = postOptional.get();
        if (!fullPost.getUserId().equals(post.getUserId())) {
            logger.error("Can't update post, User {} is not the user created post {}", post.getUserId(), fullPost.getPostId());
            throw new PostUpdateRequestNotAuthorizedException("User is not authorized");
        }
        fullPost.setText(post.getText());
        boolean updated = postRedis.update(fullPost);
        if (!updated) {
            logger.error("Can't update post, Redis response is false");
            throw new PostUpdateErrorException("Can't update post");
        }
        logger.info("Update post {}", postId);
        return fullPost;
    }

    public Optional<Post> read(Long postId) {
        return postRedis.read(postId);
    }
}
