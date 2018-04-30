package com.example.baseproject.manager;

import com.example.baseproject.MyApplication;
import com.example.baseproject.pojo.MyObjectBox;
import com.example.baseproject.pojo.UserInfoBean;
import com.example.baseproject.pojo.UserInfoBean_;
import com.example.baseproject.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Created by 03 on 2018/4/29.
 */

public class ObjectBoxManager {
    private static ObjectBoxManager instance;
    private BoxStore boxStore;

    public static final Class USERINFOCLASS = UserInfoBean.class;

    private ObjectBoxManager(){
        boxStore = MyObjectBox.builder().androidContext(MyApplication.getInstance()).build();
    }

    public synchronized static ObjectBoxManager getInstance(){
        if(null == instance){
            instance = new ObjectBoxManager();
        }
        return instance;
    }

    /**
     *获取BoxStore
     * @param cls boxStore对应的类
     * @return 返回对应的BoxStore
     */
    public Box<?> getBoxStore(Class<?> cls){
        return boxStore.boxFor(cls);
    }

    /**
     * 根据userId查询用户信息
     * @param userId 用户id
     * @return 返回查询结果
     */
    public List<?> getUserInfoByUserId(int userId){
        List<?> list = new ArrayList<>();
        try {
            Box<?> box = getBoxStore(USERINFOCLASS);
            list = box.query().equal(UserInfoBean_.userId,userId).build().find();
        }catch (Exception e){
            LogUtils.i(e.toString());
        }
        return list;
    }
}
