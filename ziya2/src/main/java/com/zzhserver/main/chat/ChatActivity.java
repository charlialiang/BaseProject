/*
    * Copyright 2015, gRPC Authors All rights reserved.
    *
    * Licensed under the Apache License, Version 2.0 (the "License");
    * you may not use this file except in compliance with the License.
    * You may obtain a copy of the License at
    *
    *     http://www.apache.org/licenses/LICENSE-2.0
    *
    * Unless required by applicable law or agreed to in writing, software
    * distributed under the License is distributed on an "AS IS" BASIS,
    * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    * See the License for the specific language governing permissions and
    * limitations under the License.
    */

package com.zzhserver.main.chat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ajguan.library.EasyRefreshLayout;
import com.ajguan.library.LoadModel;
import com.zzhserver.R;
import com.zzhserver.global.BaseActivity;
import com.zzhserver.global.Const;
import com.zzhserver.main.group.detail.GroupDetailActivity;
import com.zzhserver.main.user.UserDetailActivity;
import com.zzhserver.pojo.db.DChat;
import com.zzhserver.utils.HandlerUtils;
import com.zzhserver.utils.LogUtils;
import com.zzhserver.manager.MediaManager;
import com.zzhserver.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;

public class ChatActivity extends BaseActivity implements View.OnTouchListener {

    private TextView send_record;
    private EditText et_message;
    private ChatPresenter chatPresenter;
    private RecyclerView rv_chat;
    private EasyRefreshLayout easyLayout;

    private Toolbar toolbar;
    private int tagId;
    public static final int CAMERA_REQ = 1;
    public static final int MSG_UPDATE_POSITION = 0;
    public static final int MSG_UPDATE_INIT = 2;
    private String tagName = "";
    private int tagType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tagType = getIntent().getIntExtra(Const.TAG_TYPE, 0);
        tagId = getIntent().getIntExtra(Const.TAG_ID, 0);
        tagName = getIntent().getStringExtra(Const.TAG_NAME);
        setContentView(R.layout.activity_chat);
        initMvp();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatPresenter.remove();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    private void initView() {
        send_record = findViewById(R.id.send_record);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (tagType == 0) {
                    startActivity(new Intent(mActivity, UserDetailActivity.class).putExtra(Const.TAG_ID, tagId));
                } else {
                    startActivity(new Intent(mActivity, GroupDetailActivity.class).putExtra(Const.TAG_ID, tagId));
                }

                return true;
            }
        });
        toolbar.setTitle(tagName);
        et_message = findViewById(R.id.et_message);
        easyLayout = findViewById(R.id.easyLayout);
        rv_chat = findViewById(R.id.rv_user);
        rv_chat.setLayoutManager(new LinearLayoutManager(mActivity));
        rv_chat.setAdapter(chatPresenter.getChatAdapter());
        easyLayout.setLoadMoreModel(LoadModel.NONE);//不需要上拉
        easyLayout.addEasyEvent(new EasyRefreshLayout.EasyEvent() {
            @Override
            public void onLoadMore() {

            }

            @Override
            public void onRefreshing() {
                int size = chatPresenter.moreChatList();
                if (size == 0) {
                    ToastUtils.show("没有更多数据");
                }
                easyLayout.refreshComplete();
            }
        });
        HandlerUtils.sendMessageDelay(mHandler, MSG_UPDATE_INIT, 130);
        send_record.setOnTouchListener(this);
    }

    private void initMvp() {
        //MVP的视图层
        ChatListener view = new ChatListener() {
            @Override
            public void show() {
                showDialog();
            }

            @Override
            public void dismiss() {
                dismissDialog();
            }

            @Override
            public void update(final int position) {
                HandlerUtils.sendMessage(mHandler, MSG_UPDATE_POSITION, position, 0);
            }

            @Override
            public void finish() {
                mActivity.finish();
            }
        };
        //MVP的P层
        chatPresenter = new ChatPresenter(view, tagId);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_pic://发送图片
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQ);
                } else {
                    photoPicker();
                }
                break;
            case R.id.send_button://发送文字
                chatPresenter.sendMessage(DChat.TEXT, et_message.getText().toString());
                et_message.getText().clear();
                break;
        }
    }

    private void photoPicker() {//选择图片
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_REQ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                photoPicker();
            } else {
                ToastUtils.show("请在设置中打开相机权限");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i("resultCode = " + resultCode + ",requestCode = " + requestCode);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            //选取图片
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                chatPresenter.compUpload(photos.get(0));//压缩上传图片,成功后发出图片地址的消息
            }
        }
    }

    @Override
    public void handlerMessage(Message msg) {
        super.handlerMessage(msg);
        switch (msg.what) {
            case MSG_UPDATE_INIT:
                rv_chat.smoothScrollBy(0, 100000000);
                break;
            case MSG_UPDATE_POSITION:
                if (rv_chat.getScrollState() == 0) {//recycleView空闲
                    rv_chat.scrollToPosition(msg.arg1);
                    rv_chat.smoothScrollBy(0, 100000000);
                }
                break;
        }

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 1);
        } else {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    MediaManager.getInstance().startRecord();
                    break;
                case MotionEvent.ACTION_UP:
                    long recTime = MediaManager.getInstance().stopRecord();
                    if (recTime > 0) {
                        chatPresenter.moveFile(DChat.RECORD, new File(MediaManager.getInstance().recCachePath), recTime);
                    }
                    break;
            }
        }
        return true;
    }
}
