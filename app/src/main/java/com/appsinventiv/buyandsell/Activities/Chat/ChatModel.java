package com.appsinventiv.buyandsell.Activities.Chat;

public class ChatModel {

    String id,messageText,messageBy,imageUrl,audioUrl,videoUrl,documentUrl;
    String username,name,picUrl;
    String messageType,mediaType;
    String adId;
    long time;
    long mediaTime;

    public ChatModel(String id, String messageText, String messageBy, String imageUrl, String audioUrl, String videoUrl,
                     String documentUrl, String username, String name, String picUrl, String messageType, String mediaType,
                     String adId, long time, long mediaTime) {
        this.id = id;
        this.messageText = messageText;
        this.messageBy = messageBy;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.videoUrl = videoUrl;
        this.documentUrl = documentUrl;
        this.username = username;
        this.name = name;
        this.picUrl = picUrl;
        this.messageType = messageType;
        this.mediaType = mediaType;
        this.adId = adId;
        this.time = time;
        this.mediaTime = mediaTime;
    }

    public ChatModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageBy() {
        return messageBy;
    }

    public void setMessageBy(String messageBy) {
        this.messageBy = messageBy;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getMediaTime() {
        return mediaTime;
    }

    public void setMediaTime(long mediaTime) {
        this.mediaTime = mediaTime;
    }
}
