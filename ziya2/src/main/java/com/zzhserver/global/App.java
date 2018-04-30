package com.zzhserver.global;

import android.app.Activity;
import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;


import com.sora.global.MyEventBusIndex;
import com.zzhserver.manager.BoxManager;
import com.zzhserver.utils.AppUtils;
import com.zzhserver.utils.LogUtils;
import com.zzhserver.utils.NetChangeReceiver;

import org.greenrobot.eventbus.EventBus;

import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/6/3 0003.
 */

public class App extends Application {
    private static App instance;
    private ExecutorService threadPool;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerActivityLifecycleCallbacks(lifecycleCallbacks);
    }

    public void init() {
        AppUtils.initUtils(this);
        LogUtils.i("0");
        addNetworkBroadcast();
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
        BoxManager.INSTANCE.init(this);
        CrashHandler.getInstance().init();
        LogUtils.i("6");
    }

    private void addNetworkBroadcast() {
        LogUtils.i("2");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new NetChangeReceiver(), intentFilter);
    }

    public synchronized ExecutorService getGrpcThreadPool() {
        if (threadPool == null) {
            threadPool = Executors.newFixedThreadPool(3);
        }
        return threadPool;
    }

    public synchronized void threadShutdown() {
        if (threadPool != null) {
            threadPool.shutdownNow();
        }
    }


    Stack<Activity> mActivityStack = new Stack<Activity>();
    private ActivityLifecycleCallbacks lifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            mActivityStack.add(activity);
            LogUtils.i(activity + " Created size = " + mActivityStack.size());
        }

        @Override
        public void onActivityStarted(Activity activity) {
            LogUtils.i(activity + " Started");
        }

        @Override
        public void onActivityResumed(Activity activity) {
            LogUtils.i(activity + " Resumed");
        }

        @Override
        public void onActivityPaused(Activity activity) {
            LogUtils.i(activity + " Paused");
        }

        @Override
        public void onActivityStopped(Activity activity) {
            LogUtils.i(activity + " Stopped");
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            mActivityStack.remove(activity);
            LogUtils.i(activity + " Destroyed size = " + mActivityStack.size());
        }
    };
}
