package com.zzhserver.main.user.req

import com.google.gson.Gson
import com.zzhserver.global.Const
import com.zzhserver.main.InfoModel
import com.zzhserver.main.user.UserModel
import com.zzhserver.manager.BoxManager
import com.zzhserver.manager.GrpcManager
import com.zzhserver.pojo.db.UserBean
import com.zzhserver.pojo.db.UserBean_
import com.zzhserver.pojo.db.UserReq
import com.zzhserver.pojo.event.EventUser
import com.zzhserver.utils.LogUtils
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

/**
 * Created by Administrator on 2018/3/11.
 */
object UserReqModel {
    private val gson = Gson()
    val userReqList = ArrayList<UserReq>()//申请好友的用户列表

    fun lookReqUserList(): ArrayList<UserReq> {
        if (userReqList.isEmpty()) {
            val list = BoxManager.userReqBox.all
            LogUtils.i("lookReqUserList = $list")
            userReqList.addAll(list)
        }
        return userReqList
    }

    fun readReqUserList() {
        val list = BoxManager.userReqBox.all
        LogUtils.i("readReqUserList = $list")
        if (userReqList.isEmpty()) {
            userReqList.addAll(list)
        }
        //全部设置为已读
        for (userReq in list) {
            userReq.isUnread = false
        }
        BoxManager.userReqBox.put(list)
        for (userReq in userReqList) {
            userReq.isUnread = false
        }
    }

    //请求加好友
    fun addUserReq(tagUser: UserBean) {
        userReqList.add(UserReq(tagUser.uid, tagUser.name, tagUser.headPic, Const.STATUS_WAIT_ADD, true))
        GrpcManager.getInstance().sendAddUserReq(tagUser.uid)
    }

    //服务器返回请求好友的结果
    fun retAddUserReq(tagId: Int) {
        //申请好友
        for (userReq in userReqList) {
            LogUtils.i("userReq = $userReq,tagId = $tagId")
            if (tagId == userReq.uid) {
                userReq.status = Const.STATUS_WAIT_FRIEND
                BoxManager.userReqBox.put(userReq)
                //changeUserStatus(reqBean, Const.STATUS_WAIT_FRIEND);
                break
            }
        }
        EventBus.getDefault().post(EventUser(Const.EVENT_ADD_USER_REQ, tagId))
    }

    //同意对方加好友
    fun addUserOk(tagId: Int) {
        GrpcManager.getInstance().sendAddUserOk(tagId)
    }

    //同意对方加好友的结果
    fun retAddUserOk(tagId: Int) {
        LogUtils.i("同意对方好友 tagId = $tagId")
        //服务器已经收到了同意对方的命令
        for (userReq in userReqList) {
            if (tagId == userReq.uid) {
                changeUserStatus(userReq, Const.STATUS_OK_FRIEND)
                break
            }
        }
        InfoModel.setUserList(tagId)
        GrpcManager.getInstance().getUserList()//更新自己的信息
        EventBus.getDefault().post(EventUser(Const.EVENT_ADD_USER_OK))
    }

    //拒绝对方好友
    fun addUserRefuse(tagId: Int) {
        GrpcManager.getInstance().sendAddUserRefuse(tagId)
    }

    //拒绝对方结果
    fun retAddUserRefuse(tagId: Int) {
        for (userReq in userReqList) {
            if (tagId == userReq.uid) {
                changeUserStatus(userReq, Const.STATUS_REFUSE_FRIEND)
                break
            }
        }
        EventBus.getDefault().post(EventUser(Const.EVENT_ADD_USER_REFUSE))
    }

    //改变内存的状态和数据库的状态
    private fun changeUserStatus(userReq: UserReq, status: Int) {
        userReq.status = status//内存
        val item = BoxManager.userReqBox.query().equal(UserBean_.uid, userReq.uid.toLong()).build().findFirst()
        item?.let {
            item.status = status//数据库
            BoxManager.userReqBox.put(item)
            LogUtils.i("changeUserStatus.item = " + item)
        }
    }

    //被别人申请加好友
    fun recAddUserReq(msg: String) {
        try {
            val user = gson.fromJson(msg, UserBean::class.java)
            for (userReq in userReqList) {//已经申请过好友了,不需要再询问添加
                if (userReq.uid == user.uid && userReq.status == Const.STATUS_WAIT_FRIEND) {
                    return
                }
            }
            if (UserModel.getUserArray().get(user.uid) != null) {//已经是好友了,不需要再询问添加
                return
            }
            val userReq = UserReq(user.uid, user.name, user.headPic, Const.STATUS_REQ_FRIEND, true)
            userReqList.add(userReq)
            BoxManager.userReqBox.put(userReq)
            EventBus.getDefault().post(EventUser(Const.EVENT_BE_ADD_USER_REQ))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //被别人同意好友
    fun recAddUserOk(srcId: Int) {
        LogUtils.i("被别人同意好友 srcId = $srcId")
        for (userReq in userReqList) {
            if (srcId == userReq.uid) {
                userReq.isUnread = true
                changeUserStatus(userReq, Const.STATUS_OK_FRIEND)
                break
            }
        }
        InfoModel.setUserList(srcId)
        GrpcManager.getInstance().getUserList()//更新自己的信息
        EventBus.getDefault().post(EventUser(Const.EVENT_BE_ADD_USER_OK))
    }

    //被别人拒绝好友
    fun recAddUserRefuse(srcId: Int) {
        for (userReq in userReqList) {
            if (srcId == userReq.uid) {
                userReq.isUnread = true
                changeUserStatus(userReq, Const.STATUS_REFUSE_FRIEND)
                break
            }
        }
        EventBus.getDefault().post(EventUser(Const.EVENT_BEADD_USER_REFUSE))
    }
}