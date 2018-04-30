package com.zzhserver.pojo.bean;

import com.zzhserver.pojo.db.UserBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/24 0024.
 */

public class UserData {
    public ArrayList<UserBean> userList = new ArrayList<>();

    @Override
    public String toString() {
        return "UserData{" +
                "userList=" + userList +
                '}';
    }
}
