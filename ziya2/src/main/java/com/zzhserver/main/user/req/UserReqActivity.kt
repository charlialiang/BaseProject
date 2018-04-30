package com.zzhserver.main.user.req

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import com.chad.library.adapter.base.BaseQuickAdapter
import com.zzhserver.R
import com.zzhserver.global.BaseActivity
import com.zzhserver.global.Const
import com.zzhserver.main.user.UserDetailActivity
import com.zzhserver.main.user.search.UserSearchActivity
import com.zzhserver.pojo.event.EventUser
import com.zzhserver.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_user_req.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class UserReqActivity : BaseActivity() {
    private lateinit var userAdapter: UserReqAdapter
    private var firstCreate = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_req)
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
        toolbar.title = "申请列表"
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_search//搜索用户
                -> dialog()
            }
            return@setOnMenuItemClickListener true
        }
        rv_user.layoutManager = LinearLayoutManager(mActivity)
        UserReqModel.readReqUserList()
        userAdapter = UserReqAdapter(R.layout.user_req_item, UserReqModel.userReqList)
        rv_user.adapter = userAdapter
        userAdapter.setOnItemClickListener(BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            val user = UserReqModel.userReqList.get(position)
            val intent = Intent(mActivity, UserDetailActivity::class.java)
            intent.putExtra(Const.TAG_ID, user.getUid())
            mActivity.startActivity(intent)
        })
        userAdapter.setOnItemChildClickListener(BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
            val user = UserReqModel.userReqList.get(position)
            if (user.getStatus() == Const.STATUS_REQ_FRIEND) {
                UserReqModel.addUserOk(user.getUid())
            }
        })
    }

    private fun dialog() {
        val view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_search, null)
        val et_name = view.findViewById<EditText>(R.id.et_name)
        val rb_id = view.findViewById<RadioButton>(R.id.rb_id)
        val rb_name = view.findViewById<RadioButton>(R.id.rb_name)
        val rb_online = view.findViewById<RadioButton>(R.id.rb_online)
        rb_online.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                et_name.visibility = View.GONE
            } else {
                et_name.visibility = View.VISIBLE
            }
        }
        val dialog = AlertDialog.Builder(mActivity)
                .setMessage("搜索用户").setView(view)
                .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialogInterface, i ->
                    var type = Const.TYPE_SEARCH_ID
                    var search = et_name.text.toString().trim()
                    if (rb_id.isChecked) {
                        type = Const.TYPE_SEARCH_ID
                        if (TextUtils.isEmpty(search)) {
                            ToastUtils.show("搜索ID不能为空")
                            return@OnClickListener
                        }
                    } else if (rb_name.isChecked) {
                        type = Const.TYPE_SEARCH_NAME
                        if (TextUtils.isEmpty(search)) {
                            ToastUtils.show("搜索名称不能为空")
                            return@OnClickListener
                        }
                    } else {
                        type = Const.TYPE_SEARCH_ONLINE
                        search = ""//搜索的是在线用户,不需要带搜索的内容
                    }
                    startActivity(Intent(this@UserReqActivity, UserSearchActivity::class.java).putExtra(Const.TAG_SEARCH, type + search))
                }).setNegativeButton(android.R.string.cancel) { dialogInterface, i -> }
        dialog.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_req, menu)
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessage(msg: EventUser) {
        when (msg.event) {
            Const.EVENT_BE_ADD_USER_REQ, Const.EVENT_BE_ADD_USER_OK, Const.EVENT_BEADD_USER_REFUSE,
            Const.EVENT_ADD_USER_REQ, Const.EVENT_ADD_USER_OK, Const.EVENT_ADD_USER_REFUSE
            -> userAdapter.replaceData(UserReqModel.userReqList)
        }
    }
}
