package com.example.cykablyat.Model;

public class Post {

    private String postId;
    private String postImage;
    private String caption;
    private String publisher;
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Post(String postId, String postImage, String caption, String publisher, String location) {
        this.postId = postId;
        this.postImage = postImage;
        this.caption = caption;
        this.publisher = publisher;
        this.location = location;
    }

    public Post() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
