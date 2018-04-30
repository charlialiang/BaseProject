package com.zzhserver.main.user.search

import com.google.gson.Gson
import com.zzhserver.global.Const
import com.zzhserver.main.InfoModel
import com.zzhserver.main.user.req.UserReqModel
import com.zzhserver.manager.GrpcManager
import com.zzhserver.pojo.bean.SearchBean
import com.zzhserver.pojo.bean.UserData
import com.zzhserver.pojo.db.UserBean
import com.zzhserver.pojo.event.EventUser
import com.zzhserver.utils.LogUtils
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

/**
 * Created by Administrator on 2018/3/11.
 */
object UserSearchModel {
    private val gson = Gson()
    val userSearchList = ArrayList<UserBean>()//搜索用户的列表
    var search = ""
    val pageSize = 3
    var page = 1
    var pageMax = 0


    fun getUserSearchListSize(search: String) {
        userSearchList.clear()//初始化清空搜索列表
        page = 1
        pageMax = 0
        this.search = search
        val searchBean = SearchBean()
        searchBean.typeMsg = search
        GrpcManager.getInstance().getSearchUserListSize(gson.toJson(searchBean))//按条件搜索用户
    }

    //接收到搜索列表总数
    fun recUserSearchListSize(msg: String) {
        synchronized(userSearchList) {
            val size = msg.toInt()
            pageMax = size / pageSize + 1
            LogUtils.i("总共条数size = $size,当前page = $page ,共有page = $pageMax")
            getNextSearchUserList()
        }
    }

    fun getNextSearchUserList() {
        LogUtils.i("getNextSearchUserList")
        synchronized(userSearchList) {
            if (search.isNotEmpty()) {//防止一开始还没初始化就已经去拉去了
                if (page <= pageMax) {
                    LogUtils.i("开始获取当前page = $page")
                    val searchBean = SearchBean(search, page, pageSize)
                    GrpcManager.getInstance().getSearchUserList(gson.toJson(searchBean))//按条件搜索用户
                }
            }
        }
    }

    fun recUserSearchList(msg: String) {
        synchronized(userSearchList) {
            try {
                val userAll = gson.fromJson(msg, UserData::class.java)
                //先清空再放入缓存
                val cacheList = userAll.userList
                var positionList = ArrayList<Int>()
                //把拿回来的搜索用户,已经加过的用户处理显示已添加
                cacheList.forEachIndexed { index, dUser ->
                    if (InfoModel.uid == dUser.uid) {
                        positionList.add(index)

                    }
                    UserReqModel.userReqList.forEach {
                        if (it.uid == dUser.uid) {
                            if (it.status == Const.STATUS_WAIT_FRIEND) {
                                dUser.status = Const.STATUS_WAIT_FRIEND
                            }
                        }
                    }
                    /*if (UserModel.getUserArray().get(dUser.uid) != null) {
                    positionList.add(index)
                }*/
                    if (InfoModel.userIdList.contains(dUser.uid)) {
                        dUser.status = Const.STATUS_OK_FRIEND
                    }
                }
                positionList.forEach { cacheList.removeAt(it) }//把标记的全部移除掉
                LogUtils.i("positionList=$positionList")
                userSearchList.addAll(cacheList)//把每次返回的缓存放入在线列表中
                if (page < pageMax) {
                    page++//下次获取从下一页开始
                    EventBus.getDefault().post(EventUser(Const.EVENT_GET_USER_SEARCH_NOTIFY))
                } else {
                    EventBus.getDefault().post(EventUser(Const.EVENT_GET_USER_SEARCH_END))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}