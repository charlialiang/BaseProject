package com.zzhserver.main.user.req;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzhserver.R;
import com.zzhserver.global.Const;
import com.zzhserver.pojo.db.UserBean;
import com.zzhserver.pojo.db.UserReq;
import com.zzhserver.utils.ImageUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/5 0005.
 */

public class UserReqAdapter extends BaseQuickAdapter<UserReq, BaseViewHolder> {

    public UserReqAdapter(int layoutResId, ArrayList<UserReq> list) {
        super(layoutResId, list);
    }


    @Override
    protected void convert(BaseViewHolder viewHolder, final UserReq item) {
        TextView tvStatus = viewHolder.getView(R.id.tvStatus);
        TextView tvRefuse = viewHolder.getView(R.id.tvRefuse);
        viewHolder.setText(R.id.tvName, item.getName());
        viewHolder.setText(R.id.tvNum, String.valueOf(item.getUid()));
        ImageView ivHead = viewHolder.getView(R.id.ivHead);
        ImageUtils.load(item.getHeadPic(), ivHead, ImageUtils.USER_PIC);
        switch (item.getStatus()) {
            case Const.STATUS_WAIT_ADD:
                tvStatus.setText("添加");
                tvStatus.setBackgroundResource(R.color.colorSend);
                tvRefuse.setVisibility(View.GONE);
                tvStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserBean tagUser = new UserBean(item.getUid(), item.getName(), item.getHeadPic(), item.getStatus());
                        UserReqModel.INSTANCE.addUserReq(tagUser);
                    }
                });
                break;
            case Const.STATUS_WAIT_FRIEND:
                tvStatus.setText("等待添加");
                tvStatus.setBackgroundResource(R.color.transparent);
                tvRefuse.setVisibility(View.GONE);
                break;
            case Const.STATUS_REQ_FRIEND:
                tvStatus.setText("同意");
                tvStatus.setBackgroundResource(R.color.colorSend);
                tvRefuse.setVisibility(View.VISIBLE);
                tvStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserReqModel.INSTANCE.addUserOk(item.getUid());
                    }
                });
                tvRefuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserReqModel.INSTANCE.addUserRefuse(item.getUid());
                    }
                });
                break;
            case Const.STATUS_REFUSE_FRIEND://拒绝好友
                tvStatus.setText("已拒绝");
                tvStatus.setBackgroundResource(R.color.transparent);
                tvRefuse.setVisibility(View.GONE);
                break;
            case Const.STATUS_OK_FRIEND://在线
            case Const.STATUS_ONLINE://在线
                tvStatus.setText("已同意");
                tvStatus.setBackgroundResource(R.color.transparent);
                tvRefuse.setVisibility(View.GONE);
                break;
        }
    }
}
