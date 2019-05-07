package com.appsinventiv.buyandsell.Models;

/**
 * Created by AliAh on 08/05/2018.
 */

public class CustomerQueryModel {
    String name,number,message;
    long time;

    public CustomerQueryModel(String name, String number, String message, long time) {
        this.name = name;
        this.number = number;
        this.message = message;
        this.time = time;
    }

    public CustomerQueryModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
