package com.zzhserver.main.group.create;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.zzhserver.R;
import com.zzhserver.global.BaseActivity;
import com.zzhserver.utils.LogUtils;
import com.zzhserver.utils.ToastUtils;

import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;


public class GroupCreateActivity extends BaseActivity {
    private GroupCreatePresenter presenter;
    private RecyclerView rv_group;
    private Toolbar toolbar;
    private ImageView iv_head;
    private EditText et_name;
    private Button btn_ok;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        initMvp();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.remove();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        iv_head = findViewById(R.id.iv_head);
        et_name = findViewById(R.id.et_name);
        btn_ok = findViewById(R.id.btn_ok);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initMvp() {
        //MVP的视图层
        GroupCreateListener listener = new GroupCreateListener() {
            @Override
            public void finish() {
                mActivity.finish();
            }

            @Override
            public void show() {

            }

            @Override
            public void dismiss() {

            }
        };
        //MVP的P层
        presenter = new GroupCreatePresenter(listener);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_head:
                break;
            case R.id.btn_ok:
                String name = et_name.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.show("请输入群组名称");
                    return;
                }
                presenter.createGroup(name);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i("resultCode = " + resultCode + ",requestCode = " + requestCode);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            //选取图片
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                presenter.compUpload(photos.get(0));//压缩上传图片,成功后发出图片地址的消息
            }
        }
    }
}
