package com.example.cykablyat.Model;

public class Notifications {

    private String userId, description, postId;
    private boolean isPost;

    public Notifications() {
    }

    public Notifications(String userId, String description, String postId, boolean isPost) {
        this.userId = userId;
        this.description = description;
        this.postId = postId;
        this.isPost = isPost;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public boolean isIsPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }
}
