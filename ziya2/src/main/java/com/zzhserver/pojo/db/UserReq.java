package com.zzhserver.pojo.db;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

/**
 * Created by Administrator on 2017/12/16 0016.
 */
@Entity
public class UserReq  implements Serializable {
    @Id
    private long id;
    @Index
    private int uid;
    private String name;
    private String headPic;
    private int status;
    private boolean unread;

    public UserReq() {
    }

    public UserReq(int uid, String name, String headPic, int status, boolean unread) {
        this.uid = uid;
        this.name = name;
        this.headPic = headPic;
        this.status = status;
        this.unread = unread;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    @Override
    public String toString() {
        return "UserReqBean{" +
                "id=" + id +
                ", uid=" + uid +
                ", name='" + name + '\'' +
                ", headPic='" + headPic + '\'' +
                ", status=" + status +
                ", unread=" + unread +
                '}';
    }
}
