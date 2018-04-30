package com.zzhserver.main.chat;


import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.zzhserver.global.Const;
import com.zzhserver.manager.BoxManager;
import com.zzhserver.pojo.bean.RecordBean;
import com.zzhserver.pojo.db.DChat;
import com.zzhserver.pojo.event.EventChat;
import com.zzhserver.pojo.event.EventGroup;
import com.zzhserver.pojo.event.EventUser;
import com.zzhserver.utils.EncryptUtils;
import com.zzhserver.utils.FileUtils;
import com.zzhserver.utils.ImageUtils;
import com.zzhserver.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.OnCompressListener;

/**
 * Created by Administrator on 2017/11/5 0005.
 */

public class ChatPresenter {

    private final ChatAdapter chatAdapter;
    ChatListener view;
    LongSparseArray<DChat> sendMsgCache = new LongSparseArray<>();//发出信息缓存
    private ArrayList<DChat> chatList = new ArrayList<>();//聊天的信息内容列表
    private int tagId;
    ChatModel chatModel = ChatModel.getInstance();

    public ChatPresenter(ChatListener view, int tagId) {
        this.tagId = tagId;
        this.view = view;
        chatAdapter = new ChatAdapter(chatList);
        EventBus.getDefault().register(this);
        init();
    }

    public ChatAdapter getChatAdapter() {
        return chatAdapter;
    }

    public void remove() {
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        chatAdapter.addData(chatModel.getDBChatList(tagId));
        LogUtils.i("getDBChatList =" + chatList);
    }

    public int moreChatList() {
        if (chatList.size() > 0) {
            //最上面一条的时间
            DChat chat = chatList.get(0);
            LogUtils.i("moreChatList tagId=" + tagId + ",chat.getMsgTime()=" + chat.getMsgTime());
            List<DChat> moreList = chatModel.getMoreChatList(tagId, chat.getMsgTime());
            chatAdapter.addData(0, chatModel.getMoreChatList(tagId, chat.getMsgTime()));//添加更多的数据列表
            return moreList.size();
        } else {
            return 0;//没有数据
        }
    }

    //发出信息
    public void sendMessage(int msgType, String message) {
        chatModel.sendMessage(sendMsgCache, chatList, msgType, message, tagId);
        chatAdapter.notifyItemChanged(chatList.size() - 1);//更新显示发出去的这条数据
        view.update(chatList.size() - 1);//移动到最后一条
    }

    //已发出的信息,成功了修改发送状态&更新接收到的信息内容
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventChat msg) {
        if (view != null) {
            switch (msg.event) {
                case DChat.SEND:
                    //已发出的信息,成功了修改发送状态
                    DChat chat = sendMsgCache.get(msg.msgTime);
                    if (chat != null) {
                        chat.setSendStatus(msg.sendRet);
                        BoxManager.INSTANCE.getChatBox().put(chat);
                        int i;
                        for (i = 0; i < chatList.size(); i++) {
                            if (chatList.get(i).getMsgTime() == chat.getMsgTime()) {
                                chatAdapter.setData(i, chat);//添加并且刷新
                                break;
                            }
                        }
                    }
                    break;
                case DChat.RECEIVE:
                    chatAdapter.addData(msg.chat);
                    view.update(chatList.size() - 1);//移动到最后一条
                    break;
            }
        }
    }

    //群组删掉了,退出这个对话
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventGroup msg) {
        if (view != null) {
            switch (msg.event) {
                case Const.EVENT_DELETE_GROUP_SUCCESS:
                    view.finish();
                    break;
            }
        }
    }

    //好友删掉了,退出这个对话
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventUser msg) {
        switch (msg.event) {
            case Const.EVENT_DEL_USER:
                if (view != null) {
                    view.finish();
                }
                break;
        }
    }

    //压缩图片,上传图片,发送图片地址
    public void compUpload(String path) {//压缩图片
        ImageUtils.showResult(new File(path));//查看压缩前的大小
        ImageUtils.compress(path, new OnCompressListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(final File file) {
                moveFile(DChat.IMAGE, file, 0);
                ImageUtils.showResult(file);//查看压缩后的大小
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    //转到正常的文件夹中,修改名称,准备上传
    public void moveFile(final int msgType, final File file, final long recTime) {
        String md5 = EncryptUtils.MD5X("" + System.currentTimeMillis());
        String fileName = file.getAbsolutePath();
        LogUtils.i("movefile = " + fileName);
        String saveFilePath = "";
        if (fileName.contains(".jpg") || fileName.contains(".jpeg")) {
            saveFilePath = FileUtils.getAppFile(ImageUtils.IMG_PATH) + "/" + md5 + ".jpg";
        } else if (fileName.contains(".png")) {
            saveFilePath = FileUtils.getAppFile(ImageUtils.IMG_PATH) + "/" + md5 + ".png";
        } else if (fileName.contains(".gif")) {
            saveFilePath = FileUtils.getAppFile(ImageUtils.IMG_PATH) + "/" + md5 + ".gif";
        } else if (fileName.contains(".amr")) {
            saveFilePath = FileUtils.getAppFile(ImageUtils.RECORD_PATH) + "/" + md5 + ".amr";
        } else if (fileName.contains(".mp4")) {
            saveFilePath = FileUtils.getAppFile(ImageUtils.RECORD_PATH) + "/" + md5 + ".mp4";
        }
        if (TextUtils.isEmpty(saveFilePath)) {
            return;
        }
        File saveFile = FileUtils.makeFile(saveFilePath);
        if (FileUtils.copyFile(file, saveFile)) {//移动文件
            if (msgType == DChat.IMAGE) {
                sendMessage(msgType, saveFile.getAbsolutePath());//发出图片地址消息
            } else if (msgType == DChat.RECORD) {
                RecordBean recordBean = new RecordBean(saveFile.getAbsolutePath(), recTime);
                sendMessage(msgType, new Gson().toJson(recordBean));//发出录音的长度和地址消息
            }
            LogUtils.i("file=" + file.getAbsolutePath() + "  saveFile=" + saveFile.getAbsolutePath());
        }
    }

}
