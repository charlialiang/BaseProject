package com.zzhserver.main

import android.app.Activity
import android.content.Intent
import com.zzhserver.global.App
import com.zzhserver.global.Const
import com.zzhserver.manager.BoxManager
import com.zzhserver.manager.GrpcManager
import com.zzhserver.pojo.db.DUser
import com.zzhserver.pojo.db.DUser_
import com.zzhserver.utils.LogUtils
import com.zzhserver.utils.SPUtils
import java.util.*

/**
 * Created by Administrator on 2017/11/5 0005.
 */

object InfoModel {

    var uid: Int = 0
        private set
    var name: String? = null
        private set
    var headPic: String? = null
        private set
    var lastTime: Long = 0
        private set
    var status: Int = 0
        private set
    var callid: Long = 0
        private set
    var userList: String? = null
        private set
    val userIdList = ArrayList<Int>()

    private var firstLoading = true
    //开始登录
    fun loadingLogin(activity: Activity) {
        if(firstLoading){
            firstLoading = false
            App.getInstance().init()//开始初始化App工具
        }
        if (uid > 0) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        } else {
            uid = SPUtils.get(Const.SP_LOGIN_UID, 0) as Int
            val dUser = BoxManager.myUserBox.query().equal(DUser_.uid, uid.toLong()).build().findFirst()
            if (uid > 0 && dUser != null) {
                initUserInfo(dUser)//加入新的这个uid的数据到内存中
                gotoHome(activity, uid)//登录了直接去主界面
            } else {//未登录,需要去登录界面
                val intent = Intent(activity, LoginActivity::class.java)
                activity.startActivity(intent)
            }
        }
    }

    //退出登录
    fun logout() {
        //SPUtils.save(Const.SP_LOGIN_NAME, "");
        SPUtils.save(Const.SP_LOGIN_PASSWORD, "")
        SPUtils.save(Const.SP_LOGIN_UID, 0)
        BoxManager.myUserBox.removeAll()
        BoxManager.chatBox.removeAll()
        BoxManager.groupBox.removeAll()
        BoxManager.userBox.removeAll()
        BoxManager.userReqBox.removeAll()
        BoxManager.groupUserBox.removeAll()
        BoxManager.haveChatBox.removeAll()
        App.getInstance().threadShutdown()
        try {
            android.os.Process.killProcess(android.os.Process.myPid())
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun gotoHome(activity: Activity,uid: Int) {
        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent)
        GrpcManager.getInstance().getMyUserInfo(uid)
    }

    fun setMyUser(dUser: DUser) {
        //清掉之前这个uid的数据
        BoxManager.myUserBox.removeAll()
        //加入新的这个uid的数据
        BoxManager.myUserBox.put(dUser)
        initUserInfo(dUser)
    }


    @Synchronized
    private fun initUserInfo(dUser: DUser) {
        uid = dUser.uid
        name = dUser.name
        headPic = dUser.headPic
        lastTime = dUser.lastTime
        status = dUser.status
        callid = dUser.callid
        userList = dUser.userList
        userIdList.clear()
        val str = userList
        str?.let {
            if (str.contains(";")) {
                str.split(";".toRegex()).apply {
                    this.forEach { item ->
                        if (item.isEmpty()) return@forEach
                        userIdList.add(item.toInt())
                    }
                }
            }
        }
        LogUtils.i("userIdList = " + userIdList)
    }

    fun setUserList(uid: Int) {
        userList = userList + ";" + uid
        userIdList.add(uid)
    }
}
