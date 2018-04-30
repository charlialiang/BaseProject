package com.zzhserver.pojo.db;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by Administrator on 2017/12/16 0016.
 */
@Entity
public class UserGroup implements Serializable {
    @Id
    private long id;
    private int gid;
    private int uid;

    public UserGroup() {
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
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

    @Override
    public String toString() {
        return "UserGroup{" +
                "id=" + id +
                ", gid=" + gid +
                ", uid=" + uid +
                '}';
    }
}
