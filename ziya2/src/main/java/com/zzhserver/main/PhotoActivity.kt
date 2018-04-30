package com.zzhserver.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window

import com.bumptech.glide.Glide
import com.zzhserver.R
import com.zzhserver.global.Const
import com.zzhserver.utils.LogUtils
import kotlinx.android.synthetic.main.activity_photo.*


class PhotoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_photo)
        initView()
    }

    private fun initView() {
        val str_path = intent.getStringExtra(Const.TAG_PHOTO)
        LogUtils.i("TAG_PHOTO = " + str_path)
        Glide.with(this@PhotoActivity).load(str_path).into(imageView)
        imageView.setOnClickListener { finish() }
    }

    companion object {
        fun launchActivity(fromActivity: Context, path: String) {
            val intent = Intent()
            intent.setClass(fromActivity, PhotoActivity::class.java)
            intent.putExtra(Const.TAG_PHOTO, path)
            fromActivity.startActivity(intent)
        }
    }
}
