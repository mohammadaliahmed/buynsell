package com.appsinventiv.buyandsell.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AliAh on 29/12/2017.
 */

public class User {
    String name, username, email, password, phone, city,fcmKey;
    long time;
    String picUrl;
    String type1,type2;
    boolean numberVerified;
    String phoneCredentials;
    String mainCategory;

    public User() {
    }

    public User(String name, String username, String email, String password,
                String phone, String fcmKey, long time,boolean numberVerified,String city,String mainCategory) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.fcmKey = fcmKey;
        this.time = time;
        this.numberVerified=numberVerified;
        this.city=city;
        this.mainCategory=mainCategory;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public String getPhoneCredentials() {
        return phoneCredentials;
    }

    public void setPhoneCredentials(String phoneCredentials) {
        this.phoneCredentials = phoneCredentials;
    }

    public boolean isNumberVerified() {
        return numberVerified;
    }

    public void setNumberVerified(boolean numberVerified) {
        this.numberVerified = numberVerified;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFcmKey() {
        return fcmKey;
    }

    public void setFcmKey(String fcmKey) {
        this.fcmKey = fcmKey;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}