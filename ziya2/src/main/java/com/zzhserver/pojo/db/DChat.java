package com.zzhserver.pojo.db;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by Administrator on 2017/11/10 0010.
 */
@Entity
public class DChat implements MultiItemEntity{


    public static final int TEXT = 1;
    public static final int IMAGE = 2;
    public static final int RECORD = 3;

    public static final int READYSEND = 0;
    public static final int SEND = 1;
    public static final int RECEIVE = 2;

    public static final int SENDING = 2;
    public static final int SEND_OK = 1;
    public static final int SEND_FAIL = 0;

    @Id
    private long id;
    private String msg;
    private int tagId;
    private int srcId;
    private long msgTime;
    private long recTime;
    private int msgType;
    private int sendOrReceive;
    private int sendStatus;
    private boolean isUnread;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public int getSrcId() {
        return srcId;
    }

    public void setSrcId(int srcId) {
        this.srcId = srcId;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }

    public long getRecTime() {
        return recTime;
    }

    public void setRecTime(long recTime) {
        this.recTime = recTime;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getSendOrReceive() {
        return sendOrReceive;
    }

    public void setSendOrReceive(int sendOrReceive) {
        this.sendOrReceive = sendOrReceive;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public void setUnread(boolean unread) {
        isUnread = unread;
    }

    @Override
    public String toString() {
        return "ChatBean{" +
                "id=" + id +
                ", msg='" + msg + '\'' +
                ", tagId=" + tagId +
                ", srcId=" + srcId +
                ", msgTime=" + msgTime +
                ", recTime=" + recTime +
                ", msgType=" + msgType +
                ", sendOrReceive=" + sendOrReceive +
                ", sendStatus=" + sendStatus +
                ", isUnread=" + isUnread +
                '}';
    }

    @Override
    public int getItemType() {
        return sendOrReceive;
    }

}
