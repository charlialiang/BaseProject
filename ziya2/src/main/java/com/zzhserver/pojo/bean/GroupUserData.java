package com.zzhserver.pojo.bean;

import com.zzhserver.pojo.db.UserGroup;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/24 0024.
 */

public class GroupUserData {
    public ArrayList<UserGroup> userList = new ArrayList<>();

    @Override
    public String toString() {
        return "GroupUserData{" +
                "userList=" + userList +
                '}';
    }
}
