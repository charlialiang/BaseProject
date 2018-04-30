package com.zzhserver.main

import android.os.Bundle
import android.text.TextUtils
import android.view.View

import com.zzhserver.R
import com.zzhserver.global.App
import com.zzhserver.global.BaseActivity
import com.zzhserver.global.Const
import com.zzhserver.manager.GrpcManager
import com.zzhserver.utils.LogUtils
import com.zzhserver.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_register -> {
                val username = et_username.text.toString().trim()
                val password = et_password.text.toString().trim()
                val name = et_name.text.toString().trim()
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.show("名称不能为空")
                    return
                }
                if (TextUtils.isEmpty(username)) {
                    ToastUtils.show("用户名不能为空")
                    return
                }
                if (TextUtils.isEmpty(password)) {
                    ToastUtils.show("密码不能为空")
                    return
                }
                register(username, password, name)//开始注册
            }
        }
    }

    private fun register(username: String, password: String, name: String) {
        showDialog()
        App.getInstance().grpcThreadPool.execute {
            val result = GrpcManager.getInstance().register(username, password, name)
            LogUtils.i("result = " + result)
            dismissDialog()
            when (result) {
                Const.REGISTER_FAIL -> ToastUtils.show("注册失败")
                Const.REGISTER_SUCCESS -> {
                    ToastUtils.show("注册成功")
                    finish()
                }
                Const.REGISTER_ALREADY -> ToastUtils.show("该账号已经被注册,请重新注册其它账号")
            }
        }
    }

}
