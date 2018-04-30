package com.zzhserver.pojo.db;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

/**
 * Created by Administrator on 2017/12/16 0016.
 */
@Entity
public class UserChat implements Serializable {
    @Id
    private long id;
    @Index
    private int uid;
    private boolean unread;
    private int level;

    public UserChat() {
    }

    public UserChat(int uid, boolean unread, int level) {
        this.uid = uid;
        this.unread = unread;
        this.level = level;
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

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "UserChat{" +
                "id=" + id +
                ", uid=" + uid +
                ", unread=" + unread +
                ", level=" + level +
                '}';
    }
}
