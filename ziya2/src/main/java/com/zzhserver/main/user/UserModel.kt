package com.zzhserver.main.user

import android.util.SparseArray

import com.google.gson.Gson
import com.zzhserver.global.Const
import com.zzhserver.manager.BoxManager
import com.zzhserver.manager.GrpcManager
import com.zzhserver.main.user.req.UserReqModel
import com.zzhserver.pojo.bean.UserData
import com.zzhserver.pojo.db.*
//import com.sora.pojo.db.UserBean_;
import com.zzhserver.pojo.event.EventUser
import com.zzhserver.utils.LogUtils

import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Created by Administrator on 2017/12/30 0030.
 */

object UserModel {
    private val gson = Gson()
    private val userArray = SparseArray<UserBean>()//用户列表

    val userList: ArrayList<UserBean>
        get() {
            val userList = ArrayList<UserBean>()
            for (i in 0 until userArray.size()) {
                userList.add(userArray.valueAt(i))
            }
            return userList
        }

    //用户上下线
    fun userOnlineOffline(action: Int, uid: Int) {
        userArray.get(uid).status = action
        EventBus.getDefault().post(EventUser(Const.EVENT_STATUS_MSG, uid))
    }

    //===start===获取自己的用户好友列表
    fun getUserArray(): SparseArray<UserBean> {
        synchronized(userArray) {
            if (userArray.size() == 0) {
                val list = BoxManager.userBox.all
                for (user in list) {
                    user.status = Const.STATUS_OFFLINE
                    userArray.append(user.uid, user)
                }
                BoxManager.userBox.put(list)
                LogUtils.i("list. =>> " + list.toString())
            }
        }
        return userArray
    }

    fun recServerUserList(msg: String) {
        try {
            synchronized(userArray) {
                val userAll = gson.fromJson(msg, UserData::class.java)
                if (userAll.userList != null) {
                    userArray.clear()
                    for (userBean in userAll.userList) {
                        userArray.append(userBean.uid, userBean)
                    }
                    BoxManager.userBox.removeAll()
                    BoxManager.userBox.put(userAll.userList)
                    LogUtils.i(" userBox.getAll()=" + userAll.userList)
                    EventBus.getDefault().post(EventUser(Const.EVENT_GET_USER_LIST))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    //===end===获取自己用户列表



    //===start===申请删除用户好友
    fun delUser(tagId: Int) {
        GrpcManager.getInstance().sendDelUser(tagId)
    }

    //服务器返回的消息
    fun retDelUser(tagId: Int) {
        deleteUser(tagId)
    }

    //别人删除我好友,发过来的消息
    fun recDelUser(srcId: Int) {
        deleteUser(srcId)
    }

    private fun deleteUser(uid: Int) {
        try {
            val user = userArray.get(uid)
            if (user != null) {//删掉本地的
                userArray.remove(user.uid)
                BoxManager.userBox.query().equal(UserBean_.uid, user.uid.toLong()).build().remove()
                for (userReq in UserReqModel.userReqList) {
                    if (userReq.uid == uid) {
                        UserReqModel.userReqList.remove(userReq)
                        break
                    }
                }
                EventBus.getDefault().post(EventUser(Const.EVENT_DEL_USER, uid))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    //===end===申请删除用户好友

    //有人用户信息改变了
    fun recOtherUser(msg: String) {
        try {
            val userBean = gson.fromJson(msg, UserBean::class.java)
            val dUser = userArray.get(userBean.uid)
            dUser?.let {
                dUser.name = userBean.name
                dUser.headPic = userBean.headPic
                dUser.status = userBean.status
                val item = BoxManager.userBox.query().equal(UserBean_.uid, dUser.uid.toLong()).build().findFirst()
                item?.let {
                    item.name = userBean.name
                    item.headPic = userBean.headPic
                    item.status = userBean.status
                    BoxManager.userBox.put(item)
                    EventBus.getDefault().post(EventUser(Const.EVENT_OTHER_USER_CHANGE, dUser.uid))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun blackListUser(tagId: Int) {
        GrpcManager.getInstance().sendBlackListUser(tagId)
    }
}

