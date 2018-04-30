package com.zzhserver.pojo.db;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

/**
 * Created by Administrator on 2017/12/16 0016.
 */
@Entity
public class UserBean implements Serializable {
    @Id
    private long id;
    @Index
    private int uid;
    private String name;
    private String headPic;
    private int status;

    public UserBean() {
    }

    public UserBean(int uid, String name, String headPic, int status) {
        this.uid = uid;
        this.name = name;
        this.headPic = headPic;
        this.status = status;
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

    @Override
    public String toString() {
        return "UserBean{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", headPic='" + headPic + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserBean) {
            UserBean user = (UserBean) obj;
            return this.getUid() == user.getUid();
        }
        return false;
    }
}
