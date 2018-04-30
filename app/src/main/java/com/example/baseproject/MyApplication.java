package com.example.baseproject;

import com.example.baseproject.commom.BaseApplication;

/**
 * Created by 03 on 2018/4/29.
 */

public class MyApplication extends BaseApplication {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance(){
        return instance;
    }
}
