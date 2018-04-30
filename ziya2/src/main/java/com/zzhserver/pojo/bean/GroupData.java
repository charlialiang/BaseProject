package com.zzhserver.pojo.bean;

import com.zzhserver.pojo.db.DGroup;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/24 0024.
 */

public class GroupData {
    public ArrayList<DGroup> groupList = new ArrayList<>();

    @Override
    public String toString() {
        return "GroupData{" +
                "groupList=" + groupList +
                '}';
    }
}
