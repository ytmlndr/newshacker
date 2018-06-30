package com.newshacker.model.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newshacker.model.Model;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Post extends Model {

    private Long postId;
    private String text;
    private Integer userId;
    private Long createdAt;

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
}
