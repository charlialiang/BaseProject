package com.zzhserver.main.group;

import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zzhserver.global.Const;
import com.zzhserver.manager.GrpcManager;
import com.zzhserver.manager.BoxManager;
import com.zzhserver.pojo.bean.GroupData;
import com.zzhserver.pojo.bean.GroupUserData;
import com.zzhserver.pojo.db.DGroup;
import com.zzhserver.pojo.db.UserGroup;
import com.zzhserver.pojo.db.UserGroup_;
import com.zzhserver.pojo.event.EventGroup;
import com.zzhserver.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/12/30 0030.
 */

public class GroupModel {
    private static GroupModel instance;

    private Gson gson = new Gson();

    private GroupModel() {
    }

    public static GroupModel getInstance() {
        if (instance == null) {
            synchronized (GroupModel.class) {
                if (instance == null) {
                    instance = new GroupModel();
                }
            }
        }
        return instance;
    }

    //private ArrayList<DGroup> groupList = new ArrayList<>();//群组列表
    private SparseArray<DGroup> groupArray = new SparseArray<>();//群组列表
    //private ArrayList<GroupUserBean> groupUserList = new ArrayList<>();//群组用户列表
    private HashMap<Integer, ArrayList<UserGroup>> groupUserMap = new HashMap<>();//<Gid,群组用户列表>

    public ArrayList<DGroup> toGroupList() {
        ArrayList<DGroup> groupList = new ArrayList<>();
        SparseArray<DGroup> groupArray = getGroupArray();
        for (int i = 0; i < groupArray.size(); i++) {
            groupList.add(groupArray.valueAt(i));
        }
        return groupList;
    }

    //===start===获取自己的群组列表
    public SparseArray<DGroup> getGroupArray() {
        synchronized (groupArray) {
            if (groupArray.size() == 0) {
                List<DGroup> list = BoxManager.INSTANCE.getGroupBox().getAll();
                for (DGroup group : list) {
                    groupArray.put(group.getGid(), group);
                }
                LogUtils.i("list.size = " + list.size());
            }
        }
        return groupArray;
    }

    //根据Gid获取单个群组信息(群组列表只有一条群组信息)
    public void getGroup(int gid) {
        GrpcManager.getInstance().getGroup(gid);
    }

    public void recGroupList(String msg) {
        try {
            synchronized (groupArray) {
                GroupData groupData = gson.fromJson(msg, GroupData.class);
                groupArray.clear();
                if (groupData.groupList != null) {
                    for (DGroup group : groupData.groupList) {
                        groupArray.put(group.getGid(), group);
                    }
                }
                EventBus.getDefault().post(new EventGroup(Const.EVENT_GET_GROUP_LIST));
                BoxManager.INSTANCE.getGroupBox().removeAll();
                BoxManager.INSTANCE.getGroupBox().put(toGroupList());
                LogUtils.i("groupList = " + toGroupList());
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }
    //===end===获取自己的群组列表

    //===start===获取群组的用户列表
    public ArrayList<UserGroup> getGroupUserList(int gid) {
        synchronized (groupUserMap) {
            ArrayList<UserGroup> groupUserList = groupUserMap.get(gid);
            if (groupUserList == null || groupUserList.isEmpty()) {
                groupUserList = new ArrayList<>();
                List<UserGroup> list = BoxManager.INSTANCE.getGroupUserBox().query().equal(UserGroup_.gid, gid).build().find();
                LogUtils.i("list = " + list);
                groupUserList.addAll(list);
                groupUserMap.put(gid, groupUserList);
            }
            return groupUserList;
        }
    }

    public void getServerGroupUserList(int gid) {
        GrpcManager.getInstance().getGroupUserList(gid);
    }

    public void recGroupUserList(String msg, int gid) {
        try {
            synchronized (groupUserMap) {
                GroupUserData userAll = gson.fromJson(msg, GroupUserData.class);
                ArrayList<UserGroup> list = userAll.userList;
                if (list != null) {
                    if (gid > 0) {//单个
                        ArrayList<UserGroup> userList = new ArrayList<>();
                        for (UserGroup groupUser : list) {//放入内存
                            userList.add(groupUser);
                        }
                        groupUserMap.remove(gid);
                        groupUserMap.put(gid, userList);
                        EventBus.getDefault().post(new EventGroup(Const.EVENT_GET_GROUP_USER_LIST));
                        List<UserGroup> listFind = BoxManager.INSTANCE.getGroupUserBox().query().equal(UserGroup_.gid, gid).build().find();
                        BoxManager.INSTANCE.getGroupUserBox().remove(listFind);//移除旧的
                        BoxManager.INSTANCE.getGroupUserBox().put(userList);//插入新的
                    } else {
                        groupUserMap.clear();
                        for (UserGroup userGroup : list) {//放入内存
                            ArrayList<UserGroup> gList = groupUserMap.get(userGroup.getGid());
                            if (gList == null) {
                                gList = new ArrayList<>();
                            }
                            gList.add(userGroup);
                            groupUserMap.put(userGroup.getGid(), gList);
                        }
                        EventBus.getDefault().post(new EventGroup(Const.EVENT_GET_GROUP_USER_LIST));
                        BoxManager.INSTANCE.getGroupUserBox().removeAll();
                        BoxManager.INSTANCE.getGroupUserBox().put(list);
                    }
                    LogUtils.i(" groupUserMap =" + groupUserMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //===end===获取群组的用户列表


    //===start===建群
    //开始建群
    public void createGroup(String name, String headPic) {
        GrpcManager.getInstance().sendCreateGroup(name, headPic);
    }

    //建群的结果
    public void retCreateGroup(String msg) {
        if (msg.equals(Const.FAIL)) {
            EventBus.getDefault().post(new EventGroup(Const.EVENT_CREATE_GROUP_FAIL));
        } else {
            DGroup group = gson.fromJson(msg, DGroup.class);
            groupArray.put(group.getGid(), group);
            EventBus.getDefault().post(new EventGroup(Const.EVENT_CREATE_GROUP_SUCCESS));
            BoxManager.INSTANCE.getGroupBox().put(group);
            getServerGroupUserList(group.getGid());//重新获取群组用户列表
        }
    }
    //===end===建群

    //===start===修改群组
    //开始建群
    public void modifyGroup(int gid, String name, String headPic) {
        GrpcManager.getInstance().sendModifyGroup(gid, name, headPic);
    }

    //修改群组的结果
    public void retModifyGroup(String msg) {
        if (msg.equals(Const.FAIL)) {
            EventBus.getDefault().post(new EventGroup(Const.EVENT_MODIFY_GROUP_FAIL));
        } else {
            DGroup group = gson.fromJson(msg, DGroup.class);
            groupArray.put(group.getGid(), group);
            EventBus.getDefault().post(new EventGroup(Const.EVENT_MODIFY_GROUP_SUCCESS));
            BoxManager.INSTANCE.getGroupBox().put(group);
        }
    }
    //===end===修改群组


    //===start===加人
    //群组主动加人
    public void groupAddUser(int gid, String uidListStr) {
        GrpcManager.getInstance().sendGroupAddUser(gid, uidListStr);
    }

    //群组主动踢人
    public void groupDelUser(int gid, int tagId) {
        GrpcManager.getInstance().sendGroupDelUser(gid, tagId);
    }

    //群组主动加人的结果
    public void retGroupDoUser(String msg,int type) {
        try {
            if (msg.equals(Const.FAIL)) {
                switch (type){
                    case Const.GROUP_ADD_USER://发送结果
                        EventBus.getDefault().post(new EventGroup(Const.EVENT_GROUP_ADD_FAIL));
                        break;
                    case Const.GROUP_DEL_USER://发送结果
                        EventBus.getDefault().post(new EventGroup(Const.EVENT_GROUP_DEL_FAIL));
                        break;
                }
            } else {
                switch (type){
                    case Const.GROUP_ADD_USER://发送结果
                        EventBus.getDefault().post(new EventGroup(Const.EVENT_GROUP_ADD_SUCCESS));
                        break;
                    case Const.GROUP_DEL_USER://发送结果
                        EventBus.getDefault().post(new EventGroup(Const.EVENT_GROUP_DEL_SUCCESS));
                        break;
                }
                DGroup group = gson.fromJson(msg, DGroup.class);
                groupArray.put(group.getGid(), group);
                getServerGroupUserList(group.getGid());//重新获取群组用户列表
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    //接到群组要加我
    public void recGroupAddUser(int gid) {

    }

    //我去加群
    public void joinGroup(int gid) {
        GrpcManager.getInstance().sendJoinGroup(gid);
    }

    //我加群的结果
    public void retJoinGroup(String msg) {

    }
    //===end===加人

    //===start===踢人
    //接到群组要踢我
    public void recGroupDelUser(int gid) {

    }

    //我去退群
    public void quitGroup(int gid) {
        GrpcManager.getInstance().sendQuitGroup(gid);
    }

    //我退群的结果
    public void retQuitGroup(String msg, int gid) {
        if (msg.equals(Const.SUCCESS)) {
            groupArray.remove(gid);
            BoxManager.INSTANCE.getGroupUserBox().query().equal(UserGroup_.gid, gid).build().remove();
            EventBus.getDefault().post(new EventGroup(Const.EVENT_DELETE_GROUP_SUCCESS));
        } else {
            EventBus.getDefault().post(new EventGroup(Const.EVENT_DELETE_GROUP_FAIL));
        }
    }

    //删除群
    public void removeGroup(int gid) {
        GrpcManager.getInstance().sendRemoveGroup(gid);
    }

    //删除群的结果
    public void retRemoveGroup(String msg, int gid) {
        if (msg.equals(Const.FAIL)) {
            EventBus.getDefault().post(new EventGroup(Const.EVENT_DELETE_GROUP_FAIL));
        } else {
            groupArray.remove(gid);
            BoxManager.INSTANCE.getGroupUserBox().query().equal(UserGroup_.gid, gid).build().remove();
            EventBus.getDefault().post(new EventGroup(Const.EVENT_DELETE_GROUP_SUCCESS));
        }
    }
    //===end===踢人
}
