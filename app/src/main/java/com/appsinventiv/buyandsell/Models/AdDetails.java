package com.appsinventiv.buyandsell.Models;

import java.util.List;

/**
 * Created by maliahmed on 12/1/2017.
 */

public class AdDetails {
    String adId, title, description, username, phone, picUrl;
    long time, price;
    List<String> pictures;
    String category;

    public AdDetails() {
    }

    public AdDetails(String adId, String title, String description, String username,
                     String phone, String picUrl,
                     long time,
                     long price,  String category) {
        this.adId = adId;
        this.title = title;
        this.description = description;
        this.username = username;
        this.phone = phone;
        this.picUrl = picUrl;
        this.time = time;
        this.price = price;
        this.category = category;

    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}