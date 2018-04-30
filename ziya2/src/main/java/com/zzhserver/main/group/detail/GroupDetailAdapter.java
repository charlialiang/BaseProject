package com.zzhserver.main.group.detail;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzhserver.R;
import com.zzhserver.main.InfoModel;
import com.zzhserver.main.user.UserModel;
import com.zzhserver.pojo.db.UserBean;
import com.zzhserver.pojo.db.UserGroup;
import com.zzhserver.utils.ImageUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/11/5 0005.
 */

public class GroupDetailAdapter extends BaseQuickAdapter<UserGroup, BaseViewHolder> {


    public GroupDetailAdapter(int layoutResId, @Nullable List<UserGroup> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder viewHolder, UserGroup item) {
        ImageView ivHead = viewHolder.getView(R.id.iv_head);
        if (item.getUid() == InfoModel.INSTANCE.getUid()) {
            viewHolder.setText(R.id.tv_name, InfoModel.INSTANCE.getName());
            ImageUtils.load(InfoModel.INSTANCE.getHeadPic(), ivHead, ImageUtils.GROUP_PIC);
        } else {
            UserBean user = UserModel.INSTANCE.getUserArray().get(item.getUid());
            if (user != null) {
                viewHolder.setText(R.id.tv_name, user.getName());
                ImageUtils.load(user.getHeadPic(), ivHead, ImageUtils.GROUP_PIC);
            }
        }

    }
}
