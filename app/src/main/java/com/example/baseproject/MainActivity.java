package com.example.baseproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.baseproject.manager.ObjectBoxManager;
import com.example.baseproject.pojo.UserInfoBean;

import io.objectbox.Box;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        objectBoxTest();
    }

    private void objectBoxTest(){
        UserInfoBean userInfoBean = new UserInfoBean();
        userInfoBean.setUserId(100);
        Box box = ObjectBoxManager.getInstance().getBoxStore(ObjectBoxManager.USERINFOCLASS);
        box.put(userInfoBean);
        ObjectBoxManager.getInstance().getUserInfoByUserId(100);
    }
}
