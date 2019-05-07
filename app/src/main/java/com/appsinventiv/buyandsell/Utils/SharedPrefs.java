package com.appsinventiv.buyandsell.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import com.appsinventiv.buyandsell.ApplicationClass;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by AliAh on 20/02/2018.
 */

public class SharedPrefs {


    private SharedPrefs() {

    }


    public static void setName(String value) {

        preferenceSetter("name", value);
    }

    public static String getName() {
        return preferenceGetter("name");
    }

    public static void setUsername(String value) {

        preferenceSetter("username", value);
    }

    public static String getUsername() {
        return preferenceGetter("username");
    }

    public static void setIsLoggedIn(String value) {

        preferenceSetter("isLoggedIn", value);
    }

    public static String getIsLoggedIn() {
        return preferenceGetter("isLoggedIn");
    }


    public static void setFcmKey(String fcmKey) {
        preferenceSetter("fcmKey", fcmKey);
    }

    public static String getFcmKey() {
        return preferenceGetter("fcmKey");
    }

    public static void preferenceSetter(String key, String value) {
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String preferenceGetter(String key) {
        SharedPreferences pref;
        String value = "";
        pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        value = pref.getString(key, "");
        return value;
    }

    public static void logout() {
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}
