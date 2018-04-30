package com.zzhserver.pojo.event;

/**
 * Created by Administrator on 2018/1/11 0011.
 */

public class EventSet {
    public int event;
    public int number;
    public String msg;

    public EventSet(int event) {
        this.event = event;
    }

    public EventSet(int event, int number) {
        this.event = event;
        this.number = number;
    }

    public EventSet(int event, String msg) {
        this.event = event;
        this.msg = msg;
    }
}
