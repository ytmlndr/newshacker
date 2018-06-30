package com.newshacker.model.impl;

public class Vote {

    public enum VoteType {
        Upvote(1L),
        Downvote(-1L);

        public Long incrementValue;

        VoteType(Long incrementValue) {
            this.incrementValue = incrementValue;
        }

        public String lowercase() {
            return name().toLowerCase();
        }
    }

    private String voteId;
    private VoteType voteType;
    private Integer userId;
    private Long postId;
    private Long createdAt;

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public VoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteType voteType) {
        this.voteType = voteType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
