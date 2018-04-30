package com.zzhserver.main.user;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzhserver.R;
import com.zzhserver.global.Const;
import com.zzhserver.pojo.db.UserBean;
import com.zzhserver.ui.ItemView;
import com.zzhserver.utils.ImageUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/11/5 0005.
 */

public class ContactAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {


    public ContactAdapter(int layoutResId, @Nullable List<UserBean> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder viewHolder, UserBean item) {
        ItemView itemView = viewHolder.getView(R.id.item);
        itemView.setText1(item.getName());
        itemView.setText2(String.valueOf(item.getUid()));
        ImageUtils.load(item.getHeadPic(), itemView.getIcon(), ImageUtils.USER_PIC);
        switch (item.getStatus()) {
            case Const.STATUS_WAIT_CALL://正在呼叫
                itemView.setText4Bg(R.drawable.point_red);
                break;
            case Const.STATUS_CALLING://接通中
                itemView.setText4Bg(R.drawable.point_red);
                break;
            case Const.STATUS_ONLINE://在线
                itemView.setText4Bg(R.drawable.point_green);
                break;
            case Const.STATUS_OFFLINE://离线
            default://默认
                itemView.setText4Bg(R.drawable.point_gray);
                break;
        }
    }
}
