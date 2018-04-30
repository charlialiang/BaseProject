package com.zzhserver.pojo.db;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by Administrator on 2017/12/16 0016.
 */
@Entity
public class DGroup implements Serializable {
    @Id
    public long id;
    private int gid;
    private int admin;
    private String name;
    private String headPic;
    private String callList;
    private String userList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getCallList() {
        return callList;
    }

    public void setCallList(String callList) {
        this.callList = callList;
    }

    public String getUserList() {
        return userList;
    }

    public void setUserList(String userList) {
        this.userList = userList;
    }

    @Override
    public String toString() {
        return "DGroup{" +
                "id=" + id +
                ", gid=" + gid +
                ", admin=" + admin +
                ", name='" + name + '\'' +
                ", headPic='" + headPic + '\'' +
                ", callList='" + callList + '\'' +
                ", userList='" + userList + '\'' +
                '}';
    }
}
