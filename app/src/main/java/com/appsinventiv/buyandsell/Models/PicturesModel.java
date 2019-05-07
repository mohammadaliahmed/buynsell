package com.appsinventiv.buyandsell.Models;

/**
 * Created by AliAh on 29/03/2018.
 */

public class PicturesModel {
    String imageUrl;
    int position;


    public PicturesModel() {
    }

    public PicturesModel(String imageUrl, int position) {
        this.imageUrl = imageUrl;
        this.position = position;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
