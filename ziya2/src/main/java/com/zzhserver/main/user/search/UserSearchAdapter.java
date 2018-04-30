package com.zzhserver.main.user.search;

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

public class UserSearchAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {

    public UserSearchAdapter(int layoutResId, @Nullable List<UserBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, UserBean item) {
        ItemView itemView = viewHolder.getView(R.id.item);
        itemView.setText1(item.getName());
        itemView.setText2(String.valueOf(item.getUid()));
        ImageUtils.load(item.getHeadPic(), itemView.getIcon(), ImageUtils.USER_PIC);
        viewHolder.addOnClickListener(R.id.item);
        switch (item.getStatus()) {
            case Const.STATUS_WAIT_FRIEND:
                itemView.setText4Text("已发送请求");
                itemView.setText4Bg(R.color.transparent);
                break;
            case Const.STATUS_OK_FRIEND:
                itemView.setText4Text("已是好友");
                itemView.setText4Bg(R.color.transparent);
                break;
            default:
                itemView.setText4Text("添加");
                itemView.setText4Bg(R.color.colorSend);
                break;
        }
    }
}
