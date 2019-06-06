package com.appsinventiv.buyandsell.Models;

/**
 * Created by AliAh on 06/11/2018.
 */

public class CommentsModel {
    String id, productId, userId, picUrl, name, commentText;
    long time;

    public CommentsModel(String id, String productId, String userId, String picUrl, String name, String commentText, long time) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.picUrl = picUrl;
        this.name = name;
        this.commentText = commentText;
        this.time = time;
    }

    public CommentsModel() {
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

