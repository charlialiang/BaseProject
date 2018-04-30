package com.example.baseproject;

import com.example.baseproject.commom.BaseApplication;
import com.example.baseproject.utils.AppUtils;
import com.example.baseproject.utils.ToastUtils;

/**
 * Created by 03 on 2018/4/29.
 */

public class MyApplication extends BaseApplication {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AppUtils.initUtils(this);
    }

    public static MyApplication getInstance(){
        return instance;
    }
}
