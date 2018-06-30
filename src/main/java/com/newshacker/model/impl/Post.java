package com.newshacker.model.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newshacker.model.Model;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Post extends Model {

    private String postId;
    private String text;
    private Integer userId;
    private Long createdAt;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
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
