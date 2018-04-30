package com.zzhserver.main.group.group;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzhserver.R;
import com.zzhserver.pojo.db.DGroup;
import com.zzhserver.ui.ItemView;
import com.zzhserver.utils.ImageUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/11/5 0005.
 */

public class GroupAdapter extends BaseQuickAdapter<DGroup, BaseViewHolder> {


    public GroupAdapter(int layoutResId, @Nullable List<DGroup> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder viewHolder, DGroup item) {
        ItemView itemView = viewHolder.getView(R.id.item);
        itemView.setText1(item.getName());
        itemView.setText2(String.valueOf(item.getGid()));
        if (!TextUtils.isEmpty(item.getHeadPic())) {
            //Glide.with(mContext).load(item.getHeadPic()).into(itemView.getIcon());
            ImageUtils.load(item.getHeadPic(),itemView.getIcon(),ImageUtils.GROUP_PIC);
        }
    }
}
