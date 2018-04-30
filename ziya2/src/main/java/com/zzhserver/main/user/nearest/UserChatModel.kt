package com.zzhserver.main.user.nearest

import android.util.SparseArray
import com.zzhserver.main.user.UserModel
import com.zzhserver.manager.BoxManager
import com.zzhserver.pojo.db.UserChat
import com.zzhserver.pojo.db.UserChat_
import com.zzhserver.utils.LogUtils
import java.util.*

/**
 * Created by Administrator on 2018/3/11.
 */
object UserChatModel {
    val userChatArray = SparseArray<UserChat>()//用户讲话列表
    var maxLevel = 0
    fun getUserChatArray() {
        val list = BoxManager.haveChatBox.all
        for (userChat in list) {
            userChatArray.put(userChat.uid, userChat)
        }
        //拿到第一个level最大值,level值越大越顶端
        val haveChat = BoxManager.haveChatBox.query().orderDesc(UserChat_.level).build().findFirst()
        haveChat?.let { maxLevel = haveChat.level }
        LogUtils.i("maxLevel = $maxLevel")
    }

    val userChatList: ArrayList<UserChat>
        get() {
            val userChatList = ArrayList<UserChat>()
            for (i in 0 until userChatArray.size()) {
                userChatList.add(userChatArray.valueAt(i))
            }
            Collections.sort<UserChat>(userChatList, comparator)
            return userChatList
        }


    private val comparator = Comparator<UserChat> { o1, o2 -> o2.level - o1.level }

    //更新讲话人的顺序
    fun updateChat(uid: Int): Boolean {
        var userChat = userChatArray.get(uid)
        maxLevel++
        if (userChat == null) {//没讲过话,去好友列表找这个人
            val user = UserModel.getUserArray().get(uid)
            if (user != null) {
                userChat = UserChat(user.uid, false, maxLevel)
                userChatArray.append(uid, userChat)
            } else {//好友列表里面都没有这个人,直接返回
                return true
            }
        } else {//讲过话的,直接修改最优先
            userChat.level = maxLevel
        }
        try {
            val chat = BoxManager.haveChatBox.query().equal(UserChat_.uid, uid.toLong()).build().findFirst()
            if (chat != null) {//找到数据库中这个人,修改他是最优先的
                chat.level = maxLevel
                BoxManager.haveChatBox.put(chat)
            } else {//没找到数据库中这个人,直接插入这条数据
                BoxManager.haveChatBox.put(userChat)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun removeChat(userChat: UserChat) {
        userChatArray.remove(userChat.getUid())
        BoxManager.haveChatBox.query().equal(UserChat_.uid, userChat.getUid().toLong()).build().remove()
    }
}