package com.zzhserver.main.group.group;


import android.content.Intent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zzhserver.R;
import com.zzhserver.global.Const;
import com.zzhserver.main.chat.ChatActivity;
import com.zzhserver.main.group.GroupModel;
import com.zzhserver.pojo.db.DGroup;
import com.zzhserver.pojo.event.EventGroup;
import com.zzhserver.utils.AppUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/5 0005.
 */

public class GroupPresenter {
    private GroupAdapter groupAdapter;
    private ArrayList<DGroup> groupList = new ArrayList<>();//群组列表

    public GroupPresenter() {
        EventBus.getDefault().register(this);
        groupAdapter = new GroupAdapter(R.layout.user_item, groupList);
        groupAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //和选中的人聊天
                //startActivity(new Intent(mActivity, UserDetailActivity.class).putExtra(Const.TAG_USER, presenter.getUserArray().get(position)));
                Intent intent = new Intent(AppUtils.getAppContext(), ChatActivity.class);
                DGroup group = groupList.get(position);
                intent.putExtra(Const.TAG_TYPE, 1);
                intent.putExtra(Const.TAG_ID, group.getGid());
                intent.putExtra(Const.TAG_NAME, group.getName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AppUtils.getAppContext().startActivity(intent);
            }
        });
        groupAdapter.addData(GroupModel.getInstance().toGroupList());//初始化列表
    }

    public void remove() {
        EventBus.getDefault().unregister(this);
    }

    public GroupAdapter getGroupAdapter() {
        return groupAdapter;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventGroup msg) {
        switch (msg.event) {
            case Const.EVENT_CREATE_GROUP_SUCCESS:
            case Const.EVENT_DELETE_GROUP_SUCCESS:
            case Const.EVENT_GET_GROUP_LIST:
                groupAdapter.replaceData(GroupModel.getInstance().toGroupList());//刷新列表
                break;
        }
    }
}
