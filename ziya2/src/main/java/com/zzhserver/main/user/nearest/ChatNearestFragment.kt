package com.zzhserver.main.user.nearest

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.zzhserver.R
import com.zzhserver.global.Const
import com.zzhserver.main.chat.ChatActivity
import com.zzhserver.main.user.UserModel
import com.zzhserver.pojo.db.DChat
import com.zzhserver.pojo.event.EventChat
import com.zzhserver.pojo.event.EventUser
import kotlinx.android.synthetic.main.fragment_chat.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ChatNearestFragment : Fragment() {

    companion object {
        fun newInstance(): ChatNearestFragment {
            return ChatNearestFragment()
        }
    }

    private var firstCreate = true
    private var mView: View? = null
    private lateinit var adapter: ChatNearestAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_chat, container, false)
        EventBus.getDefault().register(this)
        return mView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        if (firstCreate) {
            firstCreate = false
            init()
        }
    }

    private fun init() {
        UserChatModel.getUserChatArray()
        rv_nearest_chat.layoutManager = LinearLayoutManager(activity)
        adapter = ChatNearestAdapter(R.layout.user_item, UserChatModel.userChatList)
        rv_nearest_chat.adapter = adapter
        adapter.setOnItemClickListener(BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            //和选中的人聊天
            val intent = Intent(activity, ChatActivity::class.java)
            val user = UserModel.getUserArray().valueAt(position)
            intent.putExtra(Const.TAG_TYPE, 0)
            intent.putExtra(Const.TAG_ID, user.uid)
            intent.putExtra(Const.TAG_NAME, user.name)
            activity?.startActivity(intent)
        })
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessage(msg: EventChat) {
        var uid = -1
        if (msg.event == DChat.SEND) {
            return
        } else if (msg.event == DChat.READYSEND) {
            uid = msg.chat.tagId
        } else if (msg.event == DChat.RECEIVE) {
            uid = msg.chat.srcId
        }
        if (UserChatModel.updateChat(uid)) return//好友列表里面都没有这个人,直接返回
        adapter.replaceData(UserChatModel.userChatList)//这里可以延迟处理,防止一直刷新,handler
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessage(msg: EventUser) {
        val userChat = UserChatModel.userChatArray.get(msg.number)
        if (userChat != null) {
            when (msg.event) {
                Const.EVENT_STATUS_MSG, Const.EVENT_OTHER_USER_CHANGE -> adapter.replaceData(UserChatModel.userChatList)
                Const.EVENT_DEL_USER -> UserChatModel.removeChat(userChat)
            }
        }
    }
}
