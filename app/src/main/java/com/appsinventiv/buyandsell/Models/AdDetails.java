package com.appsinventiv.buyandsell.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maliahmed on 12/1/2017.
 */

public class AdDetails {
    String adId, title, description, username, phone, picUrl;
    long time, price;
    List<String> pictures;
    ArrayList<String> categoryList;
    int likesCount,commentsCount;
    String city;
    String adType;

    public AdDetails() {
    }

    public AdDetails(String adId, String title, String description, String username,
                     String phone, String picUrl,
                     long time,
                     long price,ArrayList<String> categoryList,String city,String adType) {
        this.adId = adId;
        this.title = title;
        this.description = description;
        this.username = username;
        this.phone = phone;
        this.picUrl = picUrl;
        this.time = time;
        this.price = price;
        this.categoryList=categoryList;
        this.city=city;
        this.adType=adType;

    }

    public String getCity() {
        return city;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public ArrayList<String> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(ArrayList<String> categoryList) {
        this.categoryList = categoryList;
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


}