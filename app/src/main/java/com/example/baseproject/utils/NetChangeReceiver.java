package com.example.baseproject.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

//import com.sora.main.login.LoginModel;
//import com.sora.model.GrpcManager;


public class NetChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int netType = NetUtils.netType();
            LogUtils.i("netType = "+netType);
            if(netType == -1){
                LogUtils.i("网络断了");
//                GrpcManager.getInstance().shutdownChannel();
            }else {
                LogUtils.i("恢复网络");
//                GrpcManager.getInstance().getMyUserInfo(LoginModel.getInstance().getUid());
            }
        }
    }
}