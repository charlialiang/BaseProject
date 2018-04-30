package com.zzhserver.pojo.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/24 0024.
 */

public class MessageData {
    public ArrayList<MessageBean> messageList = new ArrayList<>();

    @Override
    public String toString() {
        return "MessageData{" +
                "messageList=" + messageList +
                '}';
    }
}
