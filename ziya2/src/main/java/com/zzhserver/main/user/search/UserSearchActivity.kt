package com.zzhserver.main.user.search

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import com.chad.library.adapter.base.BaseQuickAdapter
import com.zzhserver.R
import com.zzhserver.global.BaseActivity
import com.zzhserver.global.Const
import com.zzhserver.main.user.UserDetailActivity
import com.zzhserver.main.user.req.UserReqModel
import com.zzhserver.pojo.event.EventUser
import com.zzhserver.utils.HandlerUtils
import com.zzhserver.utils.LogUtils
import com.zzhserver.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_user.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class UserSearchActivity : BaseActivity() {
    private var firstCreate = false
    private lateinit var userAdapter: UserSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        EventBus.getDefault().register(this)
        firstCreate = true
    }

    override fun onDestroy() {
        super.onDestroy()
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
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        rv_user.layoutManager = LinearLayoutManager(mActivity!!)
        userAdapter = UserSearchAdapter(R.layout.user_item, UserSearchModel.userSearchList)
        rv_user.adapter = userAdapter
        userAdapter.setOnLoadMoreListener({
            HandlerUtils.sendMessageDelay(mHandler, -1, 200)
        }, rv_user)  //上拉更多
        userAdapter.setOnItemChildClickListener(BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
            val user = UserSearchModel.userSearchList.get(position)
            if (user.getStatus() == Const.STATUS_WAIT_FRIEND) {
                ToastUtils.show("已经加过了,等待确认")
            } else {
                UserReqModel.addUserReq(user)
            }
        })
        userAdapter.setOnItemClickListener(BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            val intent = Intent(mActivity, UserDetailActivity::class.java)
            intent.putExtra(Const.TAG_ID, UserSearchModel.userSearchList.get(position).getUid())
            mActivity.startActivity(intent)
        })
        val search = intent.getStringExtra(Const.TAG_SEARCH)
        UserSearchModel.getUserSearchListSize(search)//开始获取搜索用户列表总数
    }

    override fun handlerMessage(msg: Message?) {
        super.handlerMessage(msg)
        UserSearchModel.getNextSearchUserList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return true
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessage(msg: EventUser) {
        when (msg.event) {
            Const.EVENT_GET_USER_SEARCH_NOTIFY -> {
                LogUtils.i("EVENT_GET_USER_SEARCH_NOTIFY")
                userAdapter.notifyDataSetChanged()
                userAdapter.loadMoreComplete()
            }
            Const.EVENT_GET_USER_SEARCH_END -> {
                LogUtils.i("EVENT_GET_USER_SEARCH_END")
                userAdapter.loadMoreEnd()
            }
            Const.EVENT_ADD_USER_REQ ->
                UserSearchModel.userSearchList.forEachIndexed { index, value ->
                    if (value.uid == msg.number) {
                        value.setStatus(Const.STATUS_WAIT_FRIEND)
                        userAdapter.notifyItemChanged(index)
                    }
                }
        }
    }

}
