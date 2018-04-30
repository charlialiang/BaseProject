package com.zzhserver.pojo.bean;

/**
 * Created by Administrator on 2017/10/29 0029.
 */

public class LoginBean {
    public int code;
    public int uid;

    public LoginBean() {
    }

    @Override
    public String toString() {
        return "AccountBean{" +
                "code=" + code +
                ", uid=" + uid +
                '}';
    }
}
