package com.zzhserver.main.user.list

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Menu
import com.chad.library.adapter.base.BaseQuickAdapter
import com.zzhserver.R
import com.zzhserver.global.BaseActivity
import com.zzhserver.global.Const
import com.zzhserver.main.group.GroupModel
import com.zzhserver.main.group.create.GroupCreateActivity
import com.zzhserver.main.user.UserModel
import com.zzhserver.pojo.event.EventGroup
import com.zzhserver.ui.ItemView
import com.zzhserver.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_user_list.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class UserListActivity : BaseActivity() {
    private var gid: Int = 0
    private var firstCreate = false
    private val uidList = ArrayList<Int>()
    private lateinit var userListAda: UserListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        gid = intent.getIntExtra(Const.TAG_ID, 0)
        initView()
        firstCreate = true
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.setOnMenuItemClickListener {
            startActivity(Intent(mActivity, GroupCreateActivity::class.java))
            return@setOnMenuItemClickListener true
        }
        toolbar.setOnMenuItemClickListener {
            groupAddUser(gid)
            return@setOnMenuItemClickListener true
        }
        rv_user.layoutManager = LinearLayoutManager(mActivity)

        val userList = UserModel.userList
        userListAda = UserListAdapter(R.layout.user_list_item, UserModel.userList)
        rv_user.adapter = userListAda
        userListAda.setOnItemChildClickListener(BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
            val itemView = view as ItemView
            val user = userList.get(position)
            if (!itemView.isChecked) {
                itemView.isChecked = true
                uidList.add(user.getUid())
            } else {
                itemView.isChecked = false
                if (uidList.contains(user.getUid())) {
                    uidList.remove(user.getUid().toInt())
                }
            }
        })
    }

    fun groupAddUser(gid: Int) {
        var uidListStr = ""
        for (integer in uidList) {
            uidListStr += ";$integer"
        }
        if (TextUtils.isEmpty(uidListStr)) {
            ToastUtils.show("请选择成员")
            return
        }
        GroupModel.getInstance().groupAddUser(gid, uidListStr)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_req, menu)
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessage(msg: EventGroup) {
        when (msg.event) {
            Const.EVENT_DEL_USER, Const.EVENT_GET_GROUP_LIST -> userListAda.notifyDataSetChanged()//刷新列表
            Const.EVENT_GROUP_ADD_SUCCESS -> {
                ToastUtils.show("添加成功")
                finish()
            }
        }
    }
}
