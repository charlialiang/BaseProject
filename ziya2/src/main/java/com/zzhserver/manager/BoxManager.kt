package com.zzhserver.manager

import android.content.Context
import com.zzhserver.pojo.db.*
import com.zzhserver.utils.LogUtils

import io.objectbox.Box
import io.objectbox.BoxStore

/**
 * Created by Administrator on 2018/2/27.
 */

object BoxManager {
    lateinit var myUserBox: Box<DUser>
    lateinit var groupBox: Box<DGroup>
    lateinit var chatBox: Box<DChat>
    lateinit var groupUserBox: Box<UserGroup>
    lateinit var userBox: Box<UserBean>
    lateinit var userReqBox: Box<UserReq>
    lateinit var haveChatBox: Box<UserChat>
    private lateinit var boxStore: BoxStore
    private fun <T> getBoxT(entityClass: Class<T>): Box<T> {
        return boxStore.boxFor(entityClass)
    }

    fun init(context: Context) {
        LogUtils.i("3")
        boxStore = MyObjectBox.builder().androidContext(context).build()
        myUserBox = getBoxT(DUser::class.java)
        groupBox = getBoxT(DGroup::class.java)
        chatBox = getBoxT(DChat::class.java)
        groupUserBox = getBoxT(UserGroup::class.java)
        userBox = getBoxT(UserBean::class.java)
        userReqBox = getBoxT(UserReq::class.java)
        haveChatBox = getBoxT(UserChat::class.java)
        LogUtils.i("4")
    }
}
