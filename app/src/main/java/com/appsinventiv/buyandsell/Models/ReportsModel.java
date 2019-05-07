package com.appsinventiv.buyandsell.Models;

/**
 * Created by AliAh on 05/05/2018.
 */

public class ReportsModel {
    String adId,reason,description;
    long time;

    public ReportsModel() {
    }


    public ReportsModel(String adId, String reason, String description, long time) {
        this.adId = adId;
        this.reason = reason;
        this.description = description;
        this.time = time;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
