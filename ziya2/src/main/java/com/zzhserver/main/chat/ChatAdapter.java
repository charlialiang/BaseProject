package com.zzhserver.main.chat;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzhserver.R;
import com.zzhserver.global.Const;
import com.zzhserver.main.user.UserModel;
import com.zzhserver.main.PhotoActivity;
import com.zzhserver.main.InfoModel;
import com.zzhserver.manager.MediaManager;
import com.zzhserver.pojo.db.DChat;
import com.zzhserver.pojo.db.UserBean;
import com.zzhserver.utils.EncryptUtils;
import com.zzhserver.utils.ImageUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/11/5 0005.
 */

public class ChatAdapter extends BaseMultiItemQuickAdapter<DChat, BaseViewHolder> {

    public ChatAdapter(List<DChat> data) {
        super(data);
        addItemType(DChat.SEND, R.layout.chat_send);
        addItemType(DChat.RECEIVE, R.layout.chat_receive);
    }

    private ImageView curPlayView = null;

    @Override
    protected void convert(BaseViewHolder viewHolder, final DChat item) {
        TextView tvName = viewHolder.getView(R.id.tvName);
        TextView tvTime = viewHolder.getView(R.id.tvTime);
        TextView tvContent = viewHolder.getView(R.id.tvContent);
        ImageView ivHead = viewHolder.getView(R.id.ivHead);
        final ImageView ivPicture = viewHolder.getView(R.id.ivPicture);
        final ImageView ivVoice = viewHolder.getView(R.id.ivVoice);
        tvTime.setText(EncryptUtils.formatMM(item.getMsgTime()));
        if (item.getSendOrReceive() == DChat.SEND) {
            //我的名字和头像
            tvName.setText(InfoModel.INSTANCE.getName());
            ImageUtils.load(InfoModel.INSTANCE.getHeadPic(), ivHead, ImageUtils.USER_PIC);
            ProgressBar pbSending = viewHolder.getView(R.id.pbSending);
            TextView ivSendFail = viewHolder.getView(R.id.ivSendFail);
            if (item.getSendStatus() == DChat.SENDING) {//发送中,显示转圈
                pbSending.setVisibility(View.VISIBLE);
            }
            if (item.getSendStatus() == DChat.SEND_FAIL) {//发送失败,显示红点
                ivSendFail.setVisibility(View.VISIBLE);
            }
            if (item.getSendStatus() == DChat.SEND_OK) {//发送OK都隐藏
                pbSending.setVisibility(View.GONE);
                ivSendFail.setVisibility(View.GONE);
            }
            showType(item, tvContent, ivPicture, ivVoice);
        } else if (item.getSendOrReceive() == DChat.RECEIVE) {
            //别人的名字和头像
            UserBean userBean = UserModel.INSTANCE.getUserArray().get(item.getSrcId());
            if (userBean == null) {
                return;
            }
            if (!TextUtils.isEmpty(userBean.getName())) {
                tvName.setText(userBean.getName());
            }
            ImageUtils.load(userBean.getHeadPic(), ivHead, ImageUtils.USER_PIC);
            showType(item, tvContent, ivPicture, ivVoice);
        }
    }

    private void showType(final DChat item, TextView tvContent, ImageView ivPicture, final ImageView ivVoice) {
        if (item.getMsgType() == DChat.TEXT) {
            ivPicture.setVisibility(View.GONE);
            ivVoice.setVisibility(View.GONE);
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(item.getMsg());
        } else if (item.getMsgType() == DChat.IMAGE) {
            tvContent.setVisibility(View.GONE);
            ivVoice.setVisibility(View.GONE);
            ivPicture.setVisibility(View.VISIBLE);
            final String fUrl = item.getSendOrReceive() == DChat.SEND ? item.getMsg() : Const.IMG_URL + item.getMsg();
            ImageUtils.loadImg(fUrl, ivPicture, ImageUtils.SIMPLE_PIC);
            ivPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PhotoActivity.Companion.launchActivity(mContext, fUrl);
                }
            });

        } else if (item.getMsgType() == DChat.RECORD) {
            tvContent.setVisibility(View.GONE);
            ivPicture.setVisibility(View.GONE);
            ivVoice.setVisibility(View.VISIBLE);
            ivVoice.setImageResource(R.mipmap.ic_person_white);
            ivVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (curPlayView != null) {
                        curPlayView.setImageResource(R.mipmap.ic_person_white);  //上一个播放的恢复[播放按钮]
                    }
                    ivVoice.setImageResource(R.mipmap.ic_back_white);//开始播放显示[停止按钮]
                    if (curPlayView == ivVoice) {
                        curPlayView.setImageResource(R.mipmap.ic_person_white);
                        MediaManager.getInstance().stopPlay();
                        return;
                    }
                    curPlayView = ivVoice;//当前的按钮赋值到全局变量
                    curPlayView.setTag(item);//设置curPlayView的tag,
                    MediaManager.getInstance().startPlay(item.getMsg());
                    //播放完毕要恢复[播放按钮],需要延迟500毫秒缓存,UI是主线程的
                    ivVoice.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (curPlayView != null) {
                                curPlayView.setImageResource(R.mipmap.ic_person_white);
                                curPlayView = null;
                            }
                        }
                    }, item.getRecTime() + 500);
                }
            });
        }
    }
}
