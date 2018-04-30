package com.zzhserver.main.chat;

import android.support.v4.util.LongSparseArray;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zzhserver.global.Const;
import com.zzhserver.manager.GrpcManager;
import com.zzhserver.main.InfoModel;
import com.zzhserver.manager.BoxManager;
import com.zzhserver.pojo.bean.FileBean;
import com.zzhserver.pojo.bean.MessageBean;
import com.zzhserver.pojo.bean.RecordBean;
import com.zzhserver.pojo.db.DChat;
import com.zzhserver.pojo.db.DChat_;
import com.zzhserver.pojo.event.EventChat;
import com.zzhserver.utils.DownUploadUtil;
import com.zzhserver.utils.FileUtils;
import com.zzhserver.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.objectbox.query.QueryBuilder;

/**
 * Created by Administrator on 2017/12/30 0030.
 */

public class ChatModel {

    private static ChatModel instance;

    private ChatModel() {
    }

    public static ChatModel getInstance() {
        if (instance == null) {
            synchronized (ChatModel.class) {
                if (instance == null) {
                    instance = new ChatModel();
                }
            }
        }
        return instance;
    }


    private Gson gson = new Gson();

    //
    public List<DChat> getDBChatList(int tagId) {
        List<DChat> list = BoxManager.INSTANCE.getChatBox().query().equal(DChat_.srcId, tagId).or().equal(DChat_.tagId, tagId).orderDesc(DChat_.msgTime).build().find(0, 8);
        Collections.reverse(list);
        return list;
    }

    public List<DChat> getMoreChatList(int tagId, long lastTime) {
        QueryBuilder<DChat> query = BoxManager.INSTANCE.getChatBox().query().equal(DChat_.srcId, tagId).or().equal(DChat_.tagId, tagId);
        List<DChat> list = query.and().less(DChat_.msgTime, lastTime).orderDesc(DChat_.msgTime).build().find(0, 8);
        Collections.reverse(list);
        return list;
    }


    public void sendMessage(final LongSparseArray<DChat> sendMsgCache, final ArrayList<DChat> chatList, final int msgType, String msg, final int tagId) {

        final long msgTime = System.currentTimeMillis();
        final DChat chat = new DChat();
        chat.setMsgTime(msgTime);
        chat.setMsgType(msgType);
        if (msgType == DChat.RECORD) {
            RecordBean recordBean = gson.fromJson(msg, RecordBean.class);
            chat.setMsg(recordBean.path);
            chat.setRecTime(recordBean.time);
        } else {
            chat.setMsg(msg);
        }
        chat.setTagId(tagId);
        chat.setSrcId(InfoModel.INSTANCE.getUid());
        chat.setSendOrReceive(DChat.SEND);
        chat.setSendStatus(DChat.SENDING);
        sendMsgCache.put(msgTime, chat);
        chatList.add(chat);
        BoxManager.INSTANCE.getChatBox().put(chat);
        EventBus.getDefault().post(new EventChat(DChat.READYSEND, chat));

        //开始封成对象发出去
        final MessageBean message = new MessageBean();
        message.time = msgTime;
        message.tid = chat.getTagId();
        message.sid = chat.getSrcId();
        message.typ = Const.SEND_MSG;
        message.mTyp = chat.getMsgType();
        if (chat.getMsgType() == DChat.TEXT) {//文字不需要上传
            message.msg = chat.getMsg();
            GrpcManager.getInstance().sendMessage(message);
        } else if (chat.getMsgType() == DChat.IMAGE || chat.getMsgType() == DChat.RECORD) {//需要上传文件
            //开始上传
            DownUploadUtil.getInstance().upload(Const.UP_FILE_URL, chat.getMsg(), new DownUploadUtil.OnUploadListener() {
                @Override
                public void onUploadSuccess(String result) {
                    try {
                        LogUtils.i("onUploadSuccess:result=" + result);
                        FileBean fileBean = gson.fromJson(result, FileBean.class);
                        if (fileBean.code == 0) {
                            //发出图片或者录音地址,发送的不需要换成网络的地址,发给其他人就行了
                            //chat.setMsg(fileBean.data);
                            if (chat.getMsgType() == DChat.RECORD) {
                                RecordBean recordBean = new RecordBean(fileBean.data, chat.getRecTime());
                                message.msg = gson.toJson(recordBean);
                            } else {
                                message.msg = fileBean.data;
                            }
                            GrpcManager.getInstance().sendMessage(message);
                        } else {
                            LogUtils.i("onUploadSuccess:上传有问题 = " + fileBean.toString());
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onUploading(int progress) {
                    LogUtils.i("progress:" + progress);
                }

                @Override
                public void onUploadFailed() {
                    LogUtils.i("onUploadFailed!!!!");
                }
            });
        }
    }


    public void recMessage(MessageBean message) {
        //if (!recMsgCache.contains(chat.getMsgTime())) {//去重
        //recMsgCache.add(chat.getMsgTime());

        final DChat chat = new DChat();
        chat.setMsgType(message.mTyp);
        String content = "";
        chat.setTagId(message.tid);
        chat.setMsgTime(message.time);
        chat.setSrcId(message.sid);
        chat.setSendOrReceive(DChat.RECEIVE);
        chat.setUnread(true);
        switch (message.mTyp) {
            case DChat.TEXT://文本装的是内容
                content = message.msg;
                chat.setMsg(content);
                BoxManager.INSTANCE.getChatBox().put(chat);
                EventBus.getDefault().post(new EventChat(DChat.RECEIVE, chat));//更新最后一项
                break;
            case DChat.IMAGE://图片装的是url
                content = message.msg;
                chat.setMsg(content);
                BoxManager.INSTANCE.getChatBox().put(chat);
                EventBus.getDefault().post(new EventChat(DChat.RECEIVE, chat));//更新最后一项
                break;
            case DChat.RECORD://录音装的是url和长度
                RecordBean recordBean = gson.fromJson(message.msg, RecordBean.class);
                chat.setRecTime(recordBean.time);
                String url = Const.RECORD_URL + recordBean.path;
                final String savePath = FileUtils.getAppFile("record").getAbsolutePath() + "/" + recordBean.path;
                DownUploadUtil.getInstance().download(url, savePath, new DownUploadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess() {
                        chat.setMsg(savePath);
                        BoxManager.INSTANCE.getChatBox().put(chat);
                        EventBus.getDefault().post(new EventChat(DChat.RECEIVE, chat));//更新最后一项
                    }

                    @Override
                    public void onDownloading(int progress) {

                    }

                    @Override
                    public void onDownloadFailed() {

                    }
                });
                break;
        }
        LogUtils.i("发来的信息:" + chat.toString());
    }

    public void retSendMsgRet(int sendRet, long msgTime) {
        EventBus.getDefault().post(new EventChat(DChat.SEND, msgTime, sendRet));
    }
}

/*使用阿里oss上传文件
                final String upFileName = FileManager.UPLOAD_PIC_PATH + EncryptUtils.MD5X("" + System.currentTimeMillis());
                FileManager.getInstance().upFile(upFileName, file.getAbsolutePath(), new FileManager.FileUpListener() {//上传图片
                    @Override
                    public void onProgress(int progress) {

                    }

                    @Override
                    public void onSuccess() {
                        sendMessage(DChat.IMAGE, FileManager.FILE_PATH + upFileName, tagId);//发出图片地址消息
                    }

                    @Override
                    public void onFailure() {

                    }
                });*/