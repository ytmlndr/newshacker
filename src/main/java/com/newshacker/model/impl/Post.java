package com.newshacker.model.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newshacker.model.Model;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Post extends Model {

    private Long postId;
    private String text;
    private Integer userId;
    private Long createdAt;
    private Map<Vote.VoteType, Long> votes;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Map<Vote.VoteType, Long> getVotes() {
        return votes;
    }

    public void setVotes(Map<Vote.VoteType, Long> votes) {
        this.votes = votes;
    }
}
