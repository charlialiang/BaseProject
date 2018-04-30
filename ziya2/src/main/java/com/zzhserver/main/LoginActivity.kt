package com.zzhserver.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.zzhserver.R
import com.zzhserver.global.App
import com.zzhserver.global.BaseActivity
import com.zzhserver.global.Const
import com.zzhserver.manager.GrpcManager
import com.zzhserver.utils.LogUtils
import com.zzhserver.utils.SPUtils
import com.zzhserver.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {
    private var mUsername: String? = null
    private var mPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        immersive = true//开启透明
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun initView() {
        //是否之前有登录过的账号
        val username = SPUtils.get(Const.SP_LOGIN_NAME, "") as String
        if (username.isNotEmpty()) {
            et_username.setText(username)
        }
    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_login -> {
                mUsername = et_username.text.toString().trim()
                mPassword = et_password.text.toString().trim()
                if (mUsername.isNullOrEmpty()) {
                    ToastUtils.show("用户名不能为空")
                    return
                }
                if (mPassword.isNullOrEmpty()) {
                    ToastUtils.show("密码不能为空")
                    return
                }
                login()//开始登录
            }
            R.id.btn_register -> startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    fun login() {
        showDialog()
        App.getInstance().grpcThreadPool.execute {
            val loginBean = GrpcManager.getInstance().login(mUsername, mPassword)
            LogUtils.i("loginBean = " + loginBean)
            dismissDialog()
            when (loginBean.code) {
                Const.LOGIN_SUCCESS -> {
                    ToastUtils.show("登录成功")
                    SPUtils.save(Const.SP_LOGIN_NAME, mUsername)
                    SPUtils.save(Const.SP_LOGIN_PASSWORD, mPassword)
                    SPUtils.save(Const.SP_LOGIN_UID, loginBean.uid)
                    InfoModel.gotoHome(mActivity, loginBean.uid)
                    finish()
                }
                Const.LOGIN_FAIL_UNREGISTER -> ToastUtils.show("账号未注册")
                Const.LOGIN_FAIL_PASSWORD_ERROR -> ToastUtils.show("密码错误")
                Const.LOGIN_FAIL -> ToastUtils.show("登录失败")//其他情况
                else -> ToastUtils.show("没网")//默认没网
            }
        }
    }

}
