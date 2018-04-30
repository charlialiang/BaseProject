package com.zzhserver.pojo.event;

/**
 * Created by Administrator on 2018/1/11 0011.
 */

public class EventGroup {
    public int event;
    public int number;
    public String msg;

    public EventGroup(int event) {
        this.event = event;
    }

    public EventGroup(int event, int number) {
        this.event = event;
        this.number = number;
    }

    public EventGroup(int event, String msg) {
        this.event = event;
        this.msg = msg;
    }
}
