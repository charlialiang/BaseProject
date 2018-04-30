package com.zzhserver.pojo.event;

import com.zzhserver.pojo.db.DChat;

/**
 * Created by Administrator on 2018/1/11 0011.
 */

public class EventChat {
    public int event;
    public int sendRet;
    public long msgTime;
    public DChat chat;

    public EventChat(int event, DChat chat) {
        this.event = event;
        this.chat = chat;
    }

    public EventChat(int event, long msgTime,int sendRet) {
        this.event = event;
        this.msgTime = msgTime;
        this.sendRet = sendRet;
    }
}
