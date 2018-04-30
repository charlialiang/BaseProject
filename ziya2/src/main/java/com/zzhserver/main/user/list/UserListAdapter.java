package com.zzhserver.main.user.list;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzhserver.R;
import com.zzhserver.pojo.db.UserBean;
import com.zzhserver.ui.ItemView;
import com.zzhserver.utils.ImageUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/11/5 0005.
 */

public class UserListAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {


    public UserListAdapter(int layoutResId, @Nullable List<UserBean> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder viewHolder, UserBean item) {
        viewHolder.addOnClickListener(R.id.item);
        ItemView itemView = viewHolder.getView(R.id.item);
        itemView.setText1(item.getName());
        itemView.setText2(String.valueOf(item.getUid()));
        ImageUtils.load(item.getHeadPic(), itemView.getIcon(), ImageUtils.USER_PIC);
    }
}
