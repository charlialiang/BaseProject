package com.zzhserver.main.user

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
import com.zzhserver.main.group.group.GroupActivity
import com.zzhserver.main.user.req.UserReqActivity
import com.zzhserver.main.user.req.UserReqModel
import com.zzhserver.pojo.event.EventUser
import kotlinx.android.synthetic.main.fragment_contact.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class ContactFragment : Fragment(), View.OnClickListener {

    private lateinit var mView: View
    private lateinit var contactAdapter: ContactAdapter
    private var firstCreate = false

    companion object {
        fun newInstance(): ContactFragment {
            return ContactFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        firstCreate = true
        mView = inflater.inflate(R.layout.fragment_contact, container, false)
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
            init()
            firstCreate = false
        }
    }

    private fun init() {
        item_friend.setOnClickListener(this)
        item_group.setOnClickListener(this)
        rv_contact.layoutManager = LinearLayoutManager(activity)
        contactAdapter = ContactAdapter(R.layout.user_item, ArrayList())
        rv_contact.adapter = contactAdapter
        contactAdapter.setOnItemClickListener(BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            //和选中的人聊天
            val userBean = UserModel.userList[position]
            val intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra(Const.TAG_TYPE, 0)
            intent.putExtra(Const.TAG_ID, userBean.uid)
            intent.putExtra(Const.TAG_NAME, userBean.name)
            activity?.startActivity(intent)
        })
        UserModel.getUserArray()
        contactAdapter.addData(UserModel.userList)
        userTis()//看是否有人加好友
    }

    fun userTis() {//显示红点
        UserReqModel.lookReqUserList().forEach {
            if (it.isUnread) {
                item_friend.setCountVisibility(View.VISIBLE)
                return
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.item_friend -> {
                item_friend.setCountVisibility(View.GONE)
                startActivity(Intent(activity, UserReqActivity::class.java))
            }
            R.id.item_group -> startActivity(Intent(activity, GroupActivity::class.java))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessage(msg: EventUser) {
        when (msg.event) {
            Const.EVENT_STATUS_MSG, Const.EVENT_OTHER_USER_CHANGE, Const.EVENT_DEL_USER, Const.EVENT_GET_USER_LIST
            -> contactAdapter.replaceData(UserModel.userList)//刷新列表
            Const.EVENT_BE_ADD_USER_REQ, Const.EVENT_BE_ADD_USER_OK, Const.EVENT_BEADD_USER_REFUSE
            -> userTis()//提示红点
        }
    }
}
