package com.zzhserver.global;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.zzhserver.ui.DialogWait;
import com.zzhserver.utils.HandlerUtils;

/**
 * Created by Administrator on 2017/10/29 0029.
 */

public class BaseActivity extends AppCompatActivity implements HandlerUtils.OnReceiveMessageListener {
    public static String TAG = "BaseActivity";
    public Activity mActivity;
    private Dialog mDialog;
    public HandlerUtils.HandlerHolder mHandler;
    private static final int MSG_SHOW_DIALOG = 100;
    private static final int MSG_DISMISS_DIALOG = 101;
    public boolean immersive = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        mActivity = this;
        mHandler = new HandlerUtils.HandlerHolder(this);
        if (immersive) {
            initImmersive();
        }
    }

    @Override
    protected void onDestroy() {
        HandlerUtils.removeCallbacksAndMessages(mHandler);
        super.onDestroy();
    }

    //通知栏透明
    private void initImmersive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }

    //等待框
    private void initDialog() {
        if (mDialog == null) {
            mDialog = new DialogWait(this);
        }
    }

    public void showDialog() {
        HandlerUtils.sendMessage(mHandler, MSG_SHOW_DIALOG);
    }

    public void dismissDialog() {
        HandlerUtils.sendMessage(mHandler, MSG_DISMISS_DIALOG);
    }

    @Override
    public void handlerMessage(Message msg) {
        switch (msg.what) {
            case MSG_SHOW_DIALOG:
                initDialog();
                if (!mDialog.isShowing() && !isFinishing()) {
                    mDialog.show();
                }
                break;
            case MSG_DISMISS_DIALOG:
                if (mDialog != null) {
                    if (mDialog.isShowing() && !isFinishing()) {//dialog正在显示而且该activity并没有结束才关闭dialog
                        mDialog.dismiss();
                    }
                }
                break;
        }
    }
}
