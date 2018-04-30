package com.zzhserver.pojo.bean;

/**
 * Created by Administrator on 2017/12/16 0016.
 */

public class MessageBean {
    public int typ;//类型
    public long time;//时间
    public int sid;//源id
    public int tid;//目标id
    public int mTyp;//消息类型
    public String msg;//消息

    @Override
    public String toString() {
        return "MessageBean{" +
                "typ=" + typ +
                ", time=" + time +
                ", sid=" + sid +
                ", tid=" + tid +
                ", mTyp=" + mTyp +
                ", msg='" + msg + '\'' +
                '}';
    }
}
