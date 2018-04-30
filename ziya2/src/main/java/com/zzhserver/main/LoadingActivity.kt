package com.zzhserver.main

import android.os.Bundle

import com.zzhserver.R
import com.zzhserver.global.BaseActivity

/**
 * Created by Administrator on 2018/3/18.
 */

class LoadingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        mHandler.postDelayed({
            InfoModel.loadingLogin(mActivity)
            mActivity.finish()
        }, 200)
    }
}
