package com.example.baseproject.mvp;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

import com.example.baseproject.R;
import com.example.baseproject.utils.ToastUtils;
import com.example.baseproject.widget.ProgressDialog;


public abstract class BaseMvpActivity<V extends BaseMvpView, P extends BaseMvpPresenter> extends AppCompatActivity implements Handler.Callback {
    public Handler baseHandler;
    public int statusBarHeight, navigatHeight;
    public P mPresenter;
    public V mView;
    public BaseMvpActivity mActivity;
    protected abstract P createPresener();

    protected abstract V createView();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏,8.0系统只能设置不透明的activity全屏
        mActivity = this;
        //动态获取状态栏的大小
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int navigatId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            navigatHeight = getResources().getDimensionPixelSize(navigatId);
        }
        baseHandler = new Handler(this);
        mView = createView();
        mPresenter = createPresener();
        if (mPresenter != null && mView != null){
            mPresenter.onAttach(mView);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//隐藏软键盘
        if(null != imm){
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.onDettach();
        if (null != baseHandler) {
            baseHandler.removeCallbacksAndMessages(null);
            baseHandler = null;
        }
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }

    }

    @Override
    public void finish() {
        ToastUtils.cancel();
        super.finish();
    }

    public void showToast(String s) {
        ToastUtils.show(s);
    }


    public ProgressDialog dialog;

    /**
     * 显示加载提示窗
     */
    public void showProgressDialog() {
        showProgressDialog("加载中...");
    }

    /**
     * 显示加载提示窗
     *
     * @param msg 提示文字
     */
    protected void showProgressDialog(CharSequence msg) {
        showProgressDialog(msg, false);
    }

    /**
     * 显示加载提示窗
     *
     * @param msg       提示文字
     * @param canCancel 是否可手动取消
     */
    protected void showProgressDialog(CharSequence msg, boolean canCancel) {
        if (dialog == null) {
            dialog = new ProgressDialog(this,R.style.ProgressDialog);
        }
        dialog.setCanceledOnTouchOutside(canCancel);
        dialog.setCancelable(canCancel);
        dialog.setMessage(msg);
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * 关闭加载窗
     */
    public void dismissProgressDialog() {
        if (dialog != null) {
            if (dialog.isShowing() && !isFinishing()) {//dialog正在显示而且该activity并没有结束才关闭dialog
                dialog.dismiss();
            }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }



}