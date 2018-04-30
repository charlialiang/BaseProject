package com.zzhserver.main.user

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.View
import com.bumptech.glide.Glide
import com.zzhserver.R
import com.zzhserver.global.BaseActivity
import com.zzhserver.global.Const
import com.zzhserver.main.user.req.UserReqModel
import com.zzhserver.pojo.db.UserBean
import com.zzhserver.pojo.event.EventUser
import com.zzhserver.utils.LogUtils
import com.zzhserver.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_user_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class UserDetailActivity : BaseActivity() {

    private lateinit var tagUser: UserBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        val tagId = intent.getIntExtra(Const.TAG_ID, 0)
        tagUser = UserModel.getUserArray().get(tagId)
        initView()
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.title = tagUser.name
        tv_uid.text = tagUser.uid.toString()
        if (!TextUtils.isEmpty(tagUser.headPic)) {
            Glide.with(mActivity).load(tagUser.headPic).into(ivHead!!)
        }
        LogUtils.i("tagUser.status  = "+tagUser.status )
        if (tagUser.status >= Const.STATUS_OFFLINE) {
            btn_add.visibility = View.GONE
            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_del//删除用户
                    -> UserModel.delUser(tagUser.uid)
                    R.id.action_blacklist//拉黑用户
                    -> UserModel.blackListUser(tagUser.uid)
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    fun userTis() {
        ToastUtils.show("已经添加好友...")
        btn_add.text = "已添加"
        btn_add.isEnabled = false
    }

    fun delTis() {
        ToastUtils.show("已经删除好友...")
        mActivity.finish()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_add -> UserReqModel.addUserReq(tagUser)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (tagUser.status >= Const.STATUS_OFFLINE) {
            menuInflater.inflate(R.menu.menu_detail, menu)
        }
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessage(msg: EventUser) {
        when (msg.event) {
            Const.EVENT_DEL_USER -> delTis()
            Const.EVENT_ADD_USER_REQ -> userTis()
        }
    }
}
