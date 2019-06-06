package com.appsinventiv.buyandsell.Activities.Chat;


public class MediaModel {
    String id, type, url;
    long time;

    public MediaModel(String id, String type, String url, long time) {
        this.id = id;
        this.type = type;
        this.url = url;
        this.time = time;
    }


    public MediaModel() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
