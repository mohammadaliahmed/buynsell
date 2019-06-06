package com.appsinventiv.buyandsell;

import android.app.Application;
import android.os.StrictMode;

public class ApplicationClass extends Application {
    private static ApplicationClass instance;

    public static ApplicationClass getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

}
