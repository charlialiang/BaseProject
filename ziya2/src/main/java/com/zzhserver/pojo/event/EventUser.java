package com.zzhserver.pojo.event;

/**
 * Created by Administrator on 2018/1/11 0011.
 */

public class EventUser {
    public int event;
    public int number;
    public String msg;

    public EventUser(int event) {
        this.event = event;
    }

    public EventUser(int event, int number) {
        this.event = event;
        this.number = number;
    }

    public EventUser(int event, String msg) {
        this.event = event;
        this.msg = msg;
    }
}
